package com.hczhang.hummingbird.util;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by steven on 6/4/14.
 */
public class ModelUtils {

    private static Logger logger = LoggerFactory.getLogger(ModelUtils.class);

    /**
     * Gets all super types.
     *
     * @param type the type
     * @return the all super types
     */
    public static Set<Class<?>> getAllSuperTypes(final Class<?> type) {
        Set<Class<?>> result = new HashSet<Class<?>>();
        if (type != null) {
            result.add(type);
            result.addAll(getAllSuperTypes(type.getSuperclass()));
            for (Class<?> inter : type.getInterfaces()) {
                result.addAll(getAllSuperTypes(inter));
            }
        }
        return result;
    }

    /**
     * Determin {@code aClass} is sub-class of {@code superClass}
     *
     * @param aClass Source class
     * @param superClass Target class
     * @return true if source class is subclass of target class
     */
    public static boolean isSubclassOf(Class<?> aClass, Class<?> superClass) {
        boolean rlt = false;
        Set<Class<?>> superTypes = getAllSuperTypes(aClass);
        for (Class<?> st : superTypes) {
            if (st.getName().equals(superClass.getName())) {

                rlt = true;
            }
        }

        return rlt;
    }

    /**
     * Find all of constructors of {@code modelClass} which have only one {@code paramClass} parameter
     *
     * @param modelClass Target class
     * @param paramClass Parameter of constructor
     * @param includeSubclass true: include any sub class type of
     *, otherwise is false
     * @return a set of constructors
     */
    public static Set<Constructor> getConstructorWithParam(Class<?> modelClass, Class<?> paramClass, boolean includeSubclass) {
        Validate.notNull(modelClass, "Class must not be null.");
        Validate.notNull(paramClass, "ParamClass must not be null.");

        Set<Constructor> targetConstructor = new HashSet<Constructor>();

        Class<?> searchType = modelClass;

        Constructor[] cons = searchType.getConstructors();

        for (Constructor con : cons) {
            if (includeSubclass) {
                if (con.getParameterTypes().length == 1 && paramClass.isAssignableFrom(con.getParameterTypes()[0])) {
                    targetConstructor.add(con);
                }
            } else {
                if (con.getParameterTypes().length == 1 && con.getParameterTypes()[0].equals(paramClass)) {
                    targetConstructor.add(con);
                }
            }
        }

        if (targetConstructor.size() == 0) {
            logger.info("The model [{}] doesn't have constructor to receive param [{}].", modelClass.getName(), paramClass.getName());
        }

        return targetConstructor;
    }

    /**
     * Find all of methods of {@code modelClass} which have only one {@code paramClass} parameter.
     *
     * @param modelClass Target class
     * @param paramClass Parameter of constructor
     * @param includeSubclass true: include any sub class type of
     *, otherwise is false
     * @return Methods method with param
     */
    public static Set<Method> getMethodWithParam(Class<?> modelClass, Class<?> paramClass, boolean includeSubclass) {
        Validate.notNull(modelClass, "Class must not be null.");
        Validate.notNull(paramClass, "ParamClass must not be null.");

        Set<Method> targetMethods = new HashSet<Method>();

        Class<?> searchType = modelClass;

        Method[] methods = searchType.isInterface() ? searchType.getMethods() : searchType.getDeclaredMethods();
        for (Method method : methods) {
            Class<?>[] types = method.getParameterTypes();
            if (includeSubclass) {
                if (types.length == 1 && paramClass.isAssignableFrom(types[0])) {
                    targetMethods.add(method);
                }

            } else {
                if (types.length == 1 && types[0].equals(paramClass)) {
                    targetMethods.add(method);
                }
            }
        }

        if (targetMethods.size() == 0) {
            logger.info("The class [{}] doesn't have proper parameter [{}].", modelClass, paramClass);
        }

        return targetMethods;
    }

    /**
     * Gets method with param.
     *
     * @param modelClass the model class
     * @param paramClass the param class
     * @return the method with param
     */
    public static Set<Method> getMethodWithParam(Class<?> modelClass, Class<?> paramClass) {
        return getMethodWithParam(modelClass, paramClass, false);
    }

    /**
     * Find all of methods of {@code modelClass} which have two parameters
     * @param modelClass the model class
     * @param param1Class the param 1 class
     * @param param2Class the param 2 class
     * @param includeSubclass the include subclass
     * @return method with two params
     */
    public static Set<Method> getMethodWithTwoParams(Class<?> modelClass, Class<?> param1Class, Class<?> param2Class, boolean includeSubclass) {
        Validate.notNull(modelClass, "Class must not be null.");
        Validate.notNull(param1Class, "Param1Class must not be null.");
        Validate.notNull(param2Class, "Param2Class must not be null.");

        Set<Method> targetMethods = new HashSet<Method>();

        Class<?> searchType = modelClass;

        Method[] methods = searchType.isInterface() ? searchType.getMethods() : searchType.getDeclaredMethods();
        for (Method method : methods) {
            Class<?>[] types = method.getParameterTypes();
            if (includeSubclass) {
                if (types.length == 2 && param1Class.isAssignableFrom(types[0]) && param2Class.isAssignableFrom(types[1]) ) {
                    targetMethods.add(method);
                }

            } else {
                if (types.length == 2 && types[0].equals(param1Class) && types[1].equals(param2Class)) {
                    targetMethods.add(method);
                }
            }
        }

        if (targetMethods.size() == 0) {
            logger.info("The class [{}] doesn't have proper parameter ({}, {}).", modelClass, param1Class, param2Class);
        }

        return targetMethods;
    }

    /**
     * Gets method with two params.
     *
     * @param modelClass the model class
     * @param param1Class the param 1 class
     * @param param2Class the param 2 class
     * @return the method with two params
     */
    public static Set<Method> getMethodWithTwoParams(Class<?> modelClass, Class<?> param1Class, Class<?> param2Class) {
        return getMethodWithTwoParams(modelClass, param1Class, param2Class, false);
    }

    /**
     * Find only one method of {@code modelClass} which have only one {@code paramClass} parameter.
     *
     * @param modelClass Target class
     * @param paramClass Parameter of constructor
     * @return One method
     */
    public static Method getOneMethodWithParam(Class<?> modelClass, Class<?> paramClass) {

        // invoke command handler
        Set<Method> methods = getMethodWithParam(modelClass, paramClass);

        if (methods.size() > 1) {
            logger.warn("The class [{}] has more than 1 method to received the same parameter [{}]. Pick random one.", modelClass.toString(), paramClass.toString());
        }

        return methods.iterator().next();
    }

    /**
     * Gets property.
     *
     * @param pt the pt
     * @param propertyName the property name
     * @return the property
     * @throws Exception the exception
     */
    public static Object getProperty(Object pt, String propertyName) throws Exception {
        Class clazz = pt.getClass();
        Field f = clazz.getDeclaredField(propertyName);
        f.setAccessible(true);
        return f.get(pt);
    }

    /**
     * Sets property.
     *
     * @param pt the pt
     * @param propertyName the property name
     * @param value the value
     * @throws Exception the exception
     */
    public static void setProperty(Object pt, String propertyName, Object value) throws Exception {

        Class clazz = pt.getClass();
        Field f = clazz.getDeclaredField(propertyName);
        f.setAccessible(true);
        f.set(pt, value);
    }

    /**
     * Update static field.
     *
     * @param clazz the clazz
     * @param fieldName the field name
     * @param newValue the new value
     * @throws Exception the exception
     */
    public static void updateStaticField(Class clazz, String fieldName, Object newValue) throws Exception {

        if (clazz.equals(Object.class)) {
            return;
        }

        try {
            Field f = clazz.getDeclaredField(fieldName);

            f.setAccessible(true);
            f.set(null, newValue);
        } catch (NoSuchFieldException e) {
            updateStaticField(clazz.getSuperclass(), fieldName, newValue);
        }
    }

}
