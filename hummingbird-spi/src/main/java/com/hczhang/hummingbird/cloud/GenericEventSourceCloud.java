package com.hczhang.hummingbird.cloud;

import com.hczhang.hummingbird.cloud.lifecycle.InternalLifecycle;
import com.hczhang.hummingbird.cloud.lifecycle.JmxMetricsAware;
import com.hczhang.hummingbird.cloud.lifecycle.LifecycleAware;
import com.hczhang.hummingbird.cloud.metadata.AggregateCommandMetadata;
import com.hczhang.hummingbird.cloud.metadata.ServiceCommandMetadata;
import com.hczhang.hummingbird.command.Command;
import com.hczhang.hummingbird.event.Event;
import com.hczhang.hummingbird.event.Handler;
import com.hczhang.hummingbird.eventbus.EventBus;
import com.hczhang.hummingbird.eventbus.EventRouter;
import com.hczhang.hummingbird.eventbus.stub.NullEventRouter;
import com.hczhang.hummingbird.eventlog.EventLog;
import com.hczhang.hummingbird.fog.FogManager;
import com.hczhang.hummingbird.fog.stub.NullFogManager;
import com.hczhang.hummingbird.model.AggregateFactory;
import com.hczhang.hummingbird.model.AggregateRoot;
import com.hczhang.hummingbird.model.EventSourceAggregateRoot;
import com.hczhang.hummingbird.model.exception.AggregateIDInvalidException;
import com.hczhang.hummingbird.repository.AggregateRepository;
import com.hczhang.hummingbird.repository.EventSourceRepository;
import com.hczhang.hummingbird.transaction.EventSourceTransactionContext;
import com.hczhang.hummingbird.transaction.TransactionContext;
import com.hczhang.hummingbird.util.DDDException;
import com.hczhang.hummingbird.util.HBAssert;
import com.hczhang.hummingbird.util.ModelUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * A generic implementation for EventSourceCloud.
 *
 * <p>
 * <h3>Step 1:</h3> To make the cloud work, please setup the following attributes of Cloud
 *
 * <br/> Note: All of them are required.
 * <ul>
 *     <li>EventStore</li>
 * </ul>
 *
 * Based CQRS principle, setup QX Syncronize function. Please ignore this if you don't need QX system.
 * <pre>
 *     cloud.addEventLog(com.hczhang.hummingbird.eventlog.QXSyncEventLog);
 * </pre>
 * WARNING: QXSyncEventLog need repositories. Please don't forget to configure repository at step 4 if you need QX system.
 *
 * </p>
 *
 * <p>
 * <h3>Step 2:</h3> Register 1 or more Aggregate/Domain Service. The framework needs
 * to know where are events/commands come from, and they'll come to.
 * <pre>
 *     cloud.addCrystal(domainServiceObject);
 *     cloud.registerDewPrototype(ClassOfAggregate);
 * </pre>
 * </p>
 *
 * <p>
 * <h3>Step 3 (Optional): </h3>
 * Add more components.
 * Note: all of them are optional.
 * <ul>
 *     <li>Event Fog - The default fog is null.</li>
 *     <li>Event Log</li>
 *     <li>Event Router - The default router is null.</li>
 * </ul>
 * </p>
 *
 * <p>
 *
 * <h3>Step 4 (Optional):</h3>
 * Register component(s) for legacy system. <br/>
 * The framework will use AggregateFactory to load Aggregate from Legacy Database.
 * And use AggregateRepository to insert/update/delete Aggregate on Legacy Database.
 *
 * <pre>
 *     cloud.addRadiator(AggregateFactory, AggregateClass);
 *
 *     // it will be used by AggregateFactory.
 *     cloud.addRepository(AggregateRepository, ClassOfAggregate);
 * </pre>
 * </p>
 *
 * <p>
 * <h3>Step5: Start Cloud</h3>
 * <pre>
 *     cloud.launch();
 * </pre>
 * </p>
 * Created by steven on 5/21/14.
 */
public class GenericEventSourceCloud implements LegacyAdaptor, QXAdaptor, EventSourceCloud, CloudMonitor {

    private static Logger logger = LoggerFactory.getLogger(GenericEventSourceCloud.class);

    // A flag to identify if the cloud is running
    private boolean running;

    /**
     * Monitor list
     */
    protected Set<LifecycleAware> lifecycleListeners;

    /**
     * A fog manager. we could add multiple fog in cloud depends on the manager implementation.
     */
    protected FogManager fogManager;

    /**
     * The Event logs.
     */
    protected Set<EventLog> eventLogs ;

    /**
     * The Repository.
     */
    protected EventSourceRepository repository;

    /**
     * The Event Router.
     */
    protected EventRouter router;


    /**
     * An object map the command to constructor or method.
     * The cloud will search constructors and methods. <br/>
     * Key: command class name <br/>
     * Value: constructor <br/>
     */
    protected static Map<String, ServiceCommandMetadata> serviceCommandHandlers;

    /**
     * An object map the command to constructor or method.
     * The Cloud will scan constructors and methods on aggregate.
     * Key: command class name <br/>
     * Value: constructors and methods could received the command.
     */
    protected static Map<String, AggregateCommandMetadata> aggCommandHandlers;

    /**
     * An object map the domain service name to domain service instance.<br/>
     * Key: class name of service<br/>
     * Value: service object<br/>
     */
    protected static Map<String, Object> domainServices;

    /**
     * An object map the event to event handler (Method).
     * You could think as this is a cache of event methods. <br/>
     * Key: class name of event  <br/>
     * Value: event handler (method) <br/>
     */
    protected static Map<String, Method> eventsHandlers;

    /**
     * An object map the aggregate root type to aggregate id field.
     * You could think as this is a cache of aggregateID of aggregate root.<br/>
     * Key: class name of aggregate root.
     * Value: the field of aggregate id.
     */
    protected static Map<String, Field> aggregateIDs;

    /**
     * An object map the aggregate root type to aggregate factory. <br/>
     * Key: Aggregate class type
     * Value: factory class type
     */
    protected static Map<String, AggregateFactory> factories;

    /**
     * An object map the aggregate class type to repository. <br/>
     * Key: Aggregate class type
     * Value: Aggregate repository instance
     */
    protected static Map<String, AggregateRepository> repositories;

    static {
        serviceCommandHandlers = new ConcurrentHashMap<String, ServiceCommandMetadata>();
        aggCommandHandlers = new ConcurrentHashMap<String, AggregateCommandMetadata>();
        domainServices = new ConcurrentHashMap<String, Object>();
        eventsHandlers = new ConcurrentHashMap<String, Method>();
        aggregateIDs = new ConcurrentHashMap<String, Field>();
        factories = new ConcurrentHashMap<String, AggregateFactory>();
        repositories = new ConcurrentHashMap<String, AggregateRepository>();
    }

    /**
     * Instantiates a new Abstract event source cloud.
     */
    protected GenericEventSourceCloud() {
        lifecycleListeners = new CopyOnWriteArraySet<LifecycleAware>();
        eventLogs = new HashSet();

        running = false;

        // setup default components

        this.setFogManager(new NullFogManager());
        this.setEventRouter(new NullEventRouter());

    }

    @Override
    public void validate() {
        if (repositories == null) {
            throw new CloudRuntimeException("Missing configuration EventSourceRepository.");
        }

        if (fogManager == null) {
            throw new CloudRuntimeException("Missing configuration FogManager. Please setup com.hczhang.hummingbird.fog.stub.NullFogManager.");
        }

        if (router == null) {
            throw new CloudRuntimeException("Missing configuration EventRouter. Plase setup com.hczhang.hummingbird.eventbus.stub.NullEventRouter");
        }

    }

    public TransactionContext newTransaction() {

        EventSourceTransactionContext context = new EventSourceTransactionContext(this);

        return context;
    }

    @Override
    public void addRepository(AggregateRepository repository, Class<? extends AggregateRoot> aggregateType) {
        if (repository == null) {
            return;
        }
        repositories.put(aggregateType.getName(), repository);
    }

    /**
     * Gets repository.
     *
     * @param aggregateType the aggregate type
     * @return the repository
     */
    public static AggregateRepository getRepository(Class<? extends AggregateRoot> aggregateType) {
        return repositories.get(aggregateType.getName());
    }

    // Meta data methods

    @Override
    public void addRadiator(AggregateFactory factory, Class<? extends AggregateRoot> aggregateType) {
        factories.put(aggregateType.getName(), factory);
    }

    /**
     * Get a factory of aggregate root.
     *
     * @param aggregateType type of aggregate root
     * @return an instance of factory. use it to populate the latest version of aggregate.
     */
    public static AggregateFactory getRadiator(String aggregateType) {
        return factories.get(aggregateType);
    }


    @Override
    public void addCrystal(Object service) {

        this.recognizeCrystal(service.getClass());

        domainServices.put(service.getClass().getName(), service);
    }

    /**
     * Recognize a Crystal (service) class. which means the domain service
     * could be managed by framework.
     * The framework will scan a domain service class and find
     * out all of useful methods. <br/>
     * Here is what we concern:
     * <ul>
     *     <li>Command Handler</li>
     * </ul>
     * @param serviceType domain service class type
     */
    protected void recognizeCrystal(Class serviceType) {
        Set<Method> methods = ModelUtils.getMethodWithTwoParams(serviceType, TransactionContext.class, Command.class, true);

        for (Method m : methods) {
            ServiceCommandMetadata data = new ServiceCommandMetadata(serviceType, m);
            Class cmdClass = m.getParameterTypes()[1];

            if (serviceCommandHandlers.containsKey(cmdClass.getName())) {
                ServiceCommandMetadata d = serviceCommandHandlers.get(cmdClass.getName());
                logger.warn("There are conflicts when register Domain Service [{}]. " +
                                "The command[{}] has been registered by [{}] already. ",
                        serviceType.getSimpleName(),
                        cmdClass.getSimpleName(),
                        d.getClassType().getSimpleName());
            } else {
                serviceCommandHandlers.put(cmdClass.getName(), data);
            }
        }
    }

    @Override
    public void registerDewPrototype(Class<? extends AggregateRoot> aggregateType) {
        HBAssert.notNull(aggregateType, CloudRuntimeException.class, "AggregateType is null");

        Set<Constructor> cons = ModelUtils.getConstructorWithParam(aggregateType, Command.class, true);

        // register constructors
        for (Constructor con : cons) {
            Class cmdClass = con.getParameterTypes()[0];
            AggregateCommandMetadata data = new AggregateCommandMetadata(aggregateType, true, con);

            if (aggCommandHandlers.containsKey(cmdClass.getName())) {
                AggregateCommandMetadata d = aggCommandHandlers.get(cmdClass.getName());
                logger.warn("There are conflicts when register aggregate [{}]. " +
                                "The command[{}] has been registered by [{}] already. ",
                        aggregateType.getSimpleName(),
                        cmdClass.getSimpleName(),
                        d.getClassType().getSimpleName());
            } else {
                aggCommandHandlers.put(cmdClass.getName(), data);
            }
        }

        // register command methods
        Set<Method> methods = ModelUtils.getMethodWithParam(aggregateType, Command.class, true);

        for (Method m : methods) {

            AggregateCommandMetadata data = new AggregateCommandMetadata(aggregateType, false, m);
            Class cmdClass = m.getParameterTypes()[0];

            if (aggCommandHandlers.containsKey(cmdClass.getName())) {
                AggregateCommandMetadata d = aggCommandHandlers.get(cmdClass.getName());
                logger.warn("There are conflicts when register aggregate [{}]. " +
                                "The command[{}] has been registered by [{}] already. ",
                        aggregateType.getSimpleName(),
                        cmdClass.getSimpleName(),
                        d.getClassType().getSimpleName());
            } else {
                aggCommandHandlers.put(cmdClass.getName(), data);
            }
            aggCommandHandlers.put(cmdClass.getName(), data);
        }

        // register event methods
        methods = ModelUtils.getMethodWithParam(aggregateType, Event.class, true);

        for (Method m : methods) {
            Class<? extends Event> et = (Class<? extends Event>) m.getGenericParameterTypes()[0];

            Method current = eventsHandlers.get(et.getName());
            if (current != null) {
                logger.warn("Register event [{}] got a conflict. Current event handler [{}]. new handler [{}]",
                        et.getName(), current.getName(), m.getName());
            } else {
                eventsHandlers.put(et.getName(), m);
            }

        }
    }

    /**
     * Get event handler (Method)
     *
     * @param eventType the event type
     * @return event handler
     */
    public static Method getEventHandler(Class<? extends Event> eventType) {
        return eventsHandlers.get(eventType.getName());
    }

    /**
     * Register field.
     *
     * @param type the type
     * @param field the field
     */
    public void registerField(Class<? extends AggregateRoot> type, Field field) {
        aggregateIDs.put(type.getName(), field);
    }

    /**
     * Get Aggregate ID field.
     * Before call this method, you need to call
     *
     * @param type the type
     * @return iD field
     */
    public static Field getIDField(Class<? extends AggregateRoot> type) {
        return aggregateIDs.get(type.getName());
    }


    // Running methods

    public <T extends AggregateRoot> T spread(TransactionContext context, Event event, T obj) {

        context.govern(obj);

        if (obj instanceof EventSourceAggregateRoot) {
            ((EventSourceAggregateRoot) obj).applyEvent(event);
        }

        return obj;
    }


    @Override
    public <T extends AggregateRoot> T spread(TransactionContext context, Event event, Class<T> type) {

        T t = null;

        try {
            t = this.liquefy(context, event.getAggregateID(), type);

            if (t != null && t.isDeleted()) {
                throw new AggregateIDInvalidException("Aggregate[{}-{}] has been deleted. Could not spread event.", type.getName(), event.getAggregateID());
            }

            if (t == null) {
                t = type.newInstance();
            }

            context.govern(t);

            if (t instanceof EventSourceAggregateRoot) {
                ((EventSourceAggregateRoot) t).applyEvent(event);
            }

        } catch (Exception e) {
            throw new CloudRuntimeException("Cannot get or new instance of [{}]. Error message: {}",
                    type.getSimpleName(), e.getMessage());
        }

        return t;
    }

    @Override
    public <T extends AggregateRoot> T spread(TransactionContext context, Command command, T obj) {
        HBAssert.notNull(command, CloudRuntimeException.class, "Command must not be null.");

        AggregateCommandMetadata data = aggCommandHandlers.get(command.getClass().getName());

        if (data == null || !data.getClassType().equals(obj.getClass())) {

            logger.error("Missing configuration about command [{}]",
                    command.getClass().getSimpleName());

            throw new CloudRuntimeException("Aggregate configuration error.");
        }

        try {

            if (data.isConstructor()) {
                // constructor
                logger.error("Configuration is error. The command [{}] is handled by a constructor, expected is method.",
                        command.getClass().getSimpleName());

                throw new CloudRuntimeException("Configuration error.");
            } else {
                if (!context.getBeans().contains(obj)) {
                    context.govern(obj);
                }

                Method m = (Method) data.getMember();

                m.setAccessible(true);
                m.invoke(obj, command);
            }

            return obj;
        } catch (InvocationTargetException e) {

            Throwable te = e.getTargetException();
            logger.warn("Spread a command [{}] got a target exception<{}>. Message: [{}]",
                    command.getClass().getName(),
                    te.getClass().getSimpleName(),
                    te.getMessage());

            if (te instanceof DDDException) {
                DDDException re = (DDDException) te;
                throw re;
            } else {
                CloudRuntimeException me = new CloudRuntimeException("Spread a Command got a exception<{}>[{}]",
                        te.getClass().getSimpleName(),
                        te.getMessage()
                );
                throw me;
            }

        } catch (Exception e) {

            logger.error("Cannot spread command<{}>({}).", command.getClass().getSimpleName(), command.getAggregateID(), e);
            throw new CloudRuntimeException(e.getMessage());
        }
    }

    @Override
    public <T extends AggregateRoot> T spread(TransactionContext context, Command command, Class<T> type) {
        HBAssert.notNull(command, CloudRuntimeException.class, "Command must not be null.");

        AggregateCommandMetadata data = aggCommandHandlers.get(command.getClass().getName());

        if (data == null || !data.getClassType().equals(type)) {

            logger.error("Missing configuration about command [{}]",
                    command.getClass().getSimpleName());

            throw new CloudRuntimeException("Aggregate configuration error.");
        }

        try {

            T rlt = null;

            if (data.isConstructor()) {
                // constructor
                Constructor con = (Constructor) data.getMember();

                con.setAccessible(true);
                rlt = (T) con.newInstance(command);

                if (rlt instanceof EventSourceAggregateRoot) {
                    EventSourceAggregateRoot agg = (EventSourceAggregateRoot) rlt;
                    context.govern(agg);

                } else {
                    logger.error("The Aggregate [{}] should extend EventSourceAggregateRoot.",
                            type.getSimpleName());
                }
            } else {
                Method m = (Method) data.getMember();

                rlt = this.liquefy(context, command.getAggregateID(), type);

                if (rlt == null) {
                    throw new AggregateIDInvalidException("The aggregate id [{}] is invalid.", command.getAggregateID());
                }

                if (rlt.isDeleted()) {
                    throw new AggregateIDInvalidException("Aggregate[{}-{}] has been deleted. Could not spread command.", type.getName(), command.getAggregateID());
                }

                m.setAccessible(true);
                m.invoke(rlt, command);
            }

            return rlt;
        } catch (InvocationTargetException e) {

            Throwable te = e.getTargetException();
            logger.warn("Spread a command [{}] got a target exception<{}>. Message: [{}]",
                    command.getClass().getName(),
                    te.getClass().getSimpleName(),
                    te.getMessage());

            if (te instanceof DDDException) {
                DDDException re = (DDDException) te;
                throw re;
            } else {
                CloudRuntimeException me = new CloudRuntimeException("Spread a Command got a exception<{}>[{}]",
                        te.getClass().getSimpleName(),
                        te.getMessage()
                );
                throw me;
            }

        } catch (DDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Cannot spread command<{}>({}).", command.getClass().getSimpleName(), command.getAggregateID(), e);
            throw new CloudRuntimeException(e.getMessage());
        }
    }

    @Override
    public Object vapor(Command command) {
        HBAssert.notNull(command, CloudRuntimeException.class, "Command must not be null.");

        Object result = null;
        ServiceCommandMetadata smd = serviceCommandHandlers.get(command.getClass().getName());

        TransactionContext context = this.newTransaction();

        try {

            if (smd != null) {
                // Command handler comes from domain service

                Object service = domainServices.get(smd.getClassType().getName());
                Method m = smd.getMember();

                m.setAccessible(true);
                result = m.invoke(service, context, command);

                context.commit();

            } else {
                // Command handler comes from aggregate root

                AggregateCommandMetadata amd = aggCommandHandlers.get(command.getClass().getName());

                if (amd == null) {
                    logger.error("Cannot find the constructor/method receiving this command [{}]",
                            command.getClass().getSimpleName());
                    throw new CloudRuntimeException("Mis-configuration error. cannot find command handler.");
                }

                if (amd.isConstructor()) {
                    // This is construction type of command

                    // Class type = amd.getClassType();
                    Constructor con = (Constructor) amd.getMember();

                    con.setAccessible(true);
                    result = con.newInstance(command);

                    if (!(result instanceof EventSourceAggregateRoot)) {
                        logger.error("The Aggregate root class [{}] must extend EventSourceAggregateRoot",
                                result.getClass());
                    } else {
                        EventSourceAggregateRoot agg = (EventSourceAggregateRoot) result;
                        context.govern(agg);

                        context.commit();
                    }
                } else {
                    // A method received this command

                    EventSourceAggregateRoot agg = getEventSourceRepository().load(command.getAggregateID(), amd.getClassType());
                    if (agg == null) {
                        throw new AggregateIDInvalidException("The aggregate id [{}] is invalid.", command.getAggregateID());
                    }
                    context.govern(agg);

                    // Class type = amd.getClassType();
                    Method method = (Method) amd.getMember();
                    method.setAccessible(true);

                    method.invoke(agg, command);

                    // commit new events
                    context.commit();

                    result = agg;
                }

            }
        } catch (InvocationTargetException e) {

            Throwable te = e.getTargetException();
            logger.warn("Vapor a command [{}] got a target exception<{}>. Message: [{}]",
                    command.getClass().getName(),
                    te.getClass().getSimpleName(),
                    te.getMessage());

            if (te instanceof DDDException) {
                DDDException re = (DDDException) te;
                throw re;
            } else {
                CloudRuntimeException me = new CloudRuntimeException("Vapor a command got a exception<{}>[{}]",
                        te.getClass().getSimpleName(), te.getMessage());
                throw me;
            }

        } catch (DDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Cannot execute command<{}>({}).", command.getClass().getSimpleName(), command.getAggregateID(), e);
            throw new CloudRuntimeException(e.getMessage());
        }

        return result;
    }

    @Override
    public <T extends AggregateRoot> T liquefy(TransactionContext context, Object id, Class<T> type) {

        T result = null;

        Object obj = getFogManager().get(id);
        if (obj != null && (obj instanceof EventSourceAggregateRoot)) {

            EventSourceAggregateRoot aggregate = (EventSourceAggregateRoot) obj;
            context.govern(aggregate);

            getEventSourceRepository().refresh(aggregate);

            result = (T) aggregate;

        } else if (EventSourceAggregateRoot.class.isAssignableFrom(type)) {
            EventSourceAggregateRoot aggregate = getEventSourceRepository().load(id, (Class<EventSourceAggregateRoot>) type);

            if (aggregate == null) {
                return null;
            }

            context.govern(aggregate);
            result = (T) aggregate;
        }



        return result;
    }

    @Override
    public <T extends AggregateRoot> T liquefy(Object id, Class<T> type) {
        T result = null;

        TransactionContext context = new EventSourceTransactionContext(this);

        Object obj = getFogManager().get(id);
        if (obj != null && (obj instanceof EventSourceAggregateRoot)) {

            EventSourceAggregateRoot aggregate = (EventSourceAggregateRoot) obj;
            context.govern(aggregate);

            getEventSourceRepository().refresh(aggregate);

            result = (T) aggregate;

        } else if (EventSourceAggregateRoot.class.isAssignableFrom(type)) {
            EventSourceAggregateRoot aggregate = getEventSourceRepository().load(id, (Class<EventSourceAggregateRoot>) type);

            if (aggregate == null) {
                return null;
            }

            context.govern(aggregate);
            result = (T) aggregate;
        }

        return result;
    }

    // Lifecycle methods

    @Override
    public boolean isRunning() {
        return this.running;
    }

    @Override
    public void initializing() {

        // Add default listener
        this.addLifecycleListener(new InternalLifecycle());
        this.addLifecycleListener(new JmxMetricsAware());

        // invoke monitor methods
        for (LifecycleAware la : lifecycleListeners) {
            la.preInit(this);
        }

        logger.info("Cloud is initializing.........................");

        validate();

        // invoke monitor methods
        for (LifecycleAware la : lifecycleListeners) {
            la.postInit(this);
        }

        this.starting();
    }

    @Override
    public void starting() {

        // invoke monitor methods
        for (LifecycleAware la : lifecycleListeners) {
            la.preStart(this);
        }

        logger.info("Cloud is starting..........................");
        this.running = true;

        // invoke monitor methods
        for (LifecycleAware la : lifecycleListeners) {
            la.postStart(this);
        }
    }

    @Override
    public void stopping() {
        // inovke monitor methods
        for (LifecycleAware la : lifecycleListeners) {
            la.preClose(this);
        }

        logger.info("Cloud is stopping..........................");
        this.running = false;

        // inovke monitor methods
        for (LifecycleAware la : lifecycleListeners) {
            la.postClose(this);
        }

    }


    // Cloud methods

    public void config() {

    }

    /**
     * Please invoke this method to start cloud running.
     */
    public void launch() {
        System.out.println(
                "            __ __                 _           __   _        __\n" +
                "           / // /_ ____ _  __ _  (_)__  ___ _/ /  (_)______/ /\n" +
                "          / _  / // /  ' \\/  ' \\/ / _ \\/ _ `/ _ \\/ / __/ _  / \n" +
                "         /_//_/\\_,_/_/_/_/_/_/_/_/_//_/\\_, /_.__/_/_/  \\_,_/  \n" +
                "                                      /___/                   \n" +
                "                                                                "

        );

        config();

        this.initializing();
    }

    /**
     * Use this method to shutdown a cloud safely.
     */
    public void shutdown() {
        this.stopping();
    }

    // Monitor methods

    @Override
    public void addLifecycleListener(LifecycleAware listener) {
        this.lifecycleListeners.add(listener);
    }

    @Override
    public void removeLifecycleListener(LifecycleAware listener) {
        this.lifecycleListeners.remove(listener);
    }

    /**
     * Sets lifecycle listeners.
     *
     * @param lifecycleListeners the lifecycle listeners
     */
    public void setLifecycleListeners(Set<LifecycleAware> lifecycleListeners) {
        this.lifecycleListeners = lifecycleListeners;
    }

    /**
     * Gets lifecycle listeners.
     *
     * @return the lifecycle listeners
     */
    public Set<LifecycleAware> getLifecycleListeners() {
        return lifecycleListeners;
    }

    // Event Handler

    @Override
    public void addEventHandler(Class<? extends Handler> handlerType) {
        Validate.notNull(handlerType, "Handler type is null");

        Handler handler = null;
        try {
            handler = handlerType.newInstance();
        } catch (Exception e) {
            logger.error("The Handler class [{}] cannot be created.", handlerType.getName());
            throw new CloudRuntimeException(e.getMessage());
        }
        addEventHandler(handler);
    }

    @Override
    public void addEventHandler(Handler handler) {
        EventBus bus = getEventRouter().getEventBus(handler.getEventType());

        if (bus == null) {
            logger.error("The related event bus has not existed, which received event type [{}]", handler.getClass().getName());
            throw new CloudRuntimeException("Cannot find related bus.");
        } else {
            bus.subscribe(handler);
        }
    }

    @Override
    public FogManager getFogManager() {
        return fogManager;
    }

    /**
     * Sets fog manager.
     *
     * @param fogManager the fog manager
     */
    public void setFogManager(FogManager fogManager) {
        this.fogManager = fogManager;
    }

    @Override
    public EventSourceRepository getEventSourceRepository() {
        return repository;
    }

    @Override
    public EventRouter getEventRouter() {
        return router;
    }

    /**
     * Sets event router.
     *
     * @param router the router
     */
    public void setEventRouter(EventRouter router) {
        this.router = router;
    }

    @Override
    public Set<EventLog> getEventLogs() {
        return eventLogs;
    }

    /**
     * Sets event logs.
     *
     * @param eventLogs the event logs
     */
    public void setEventLogs(Set<EventLog> eventLogs) {
        this.eventLogs.addAll(eventLogs);
    }

    /**
     * Add an event log.
     *
     * @param log the log
     */
    public void addEventLog(EventLog log) {
        this.eventLogs.add(log);
    }

    /**
     * Sets event source repository.
     *
     * @param eventRepository the event repository
     */
    public void setEventSourceRepository(EventSourceRepository eventRepository) {
        this.repository = eventRepository;
    }

}
