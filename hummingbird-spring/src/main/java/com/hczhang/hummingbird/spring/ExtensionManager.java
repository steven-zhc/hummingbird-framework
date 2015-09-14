package com.hczhang.hummingbird.spring;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

/**
 * Created by steven on 7/15/15.
 */
public class ExtensionManager {

    private static Logger logger = LoggerFactory.getLogger(ExtensionManager.class);

    public static final String BINDING_FILE_NAME = "META-INF/hummingbird.bindings";
    public static final String EXTENSION_POINT = "hummingbird.ext.class";

    private List<ExtensionBinding> bindings;

    private ExtensionManager() {
        List<String> exts = getBinding(EXTENSION_POINT);

        bindings = new ArrayList<ExtensionBinding>();

        for (String s : exts) {
            try {
                ExtensionBinding e = (ExtensionBinding) Class.forName(s).newInstance();
                bindings.add(e);
            } catch (Exception e) {
                logger.warn("Cannot create extension class [{}]", s, e);
            }
        }
    }

    private static ExtensionManager mgr;

    public static ExtensionManager sharedInstance() {
        if (mgr == null) {
            mgr = new ExtensionManager();

        }

        return mgr;
    }

    public List<ExtensionBinding> getBindings() {
        return bindings;
    }

    protected static List<Properties> loadResources(final String name, final ClassLoader classLoader) {

        List<Properties> list = new ArrayList<Properties>();
        Enumeration<URL> resources = null;

        try {
            resources = (classLoader == null ? ClassLoader.getSystemClassLoader() : classLoader)
                    .getResources(name);
        } catch (IOException e) {
            logger.warn("IOException: [{}]", e.getClass().getSimpleName(), e);

            return list;
        }

        while (resources != null && resources.hasMoreElements()) {
            Properties p = new Properties();

            URL url = resources.nextElement();
            InputStream input = null;
            try {
                logger.debug("Finding extension binding file: {}", url.toString());
                input = url.openStream();
                p.load(input);

                list.add(p);

            } catch (IOException e) {
                logger.warn("Cannot open config file: [{}]", url.toString(), e);
            } finally {
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {
                        logger.error("Close input error", e);
                    }
                }
            }
        }

        return list;
    }

    protected static List<String> getBinding(String name) {
        List<Properties> list = loadResources(BINDING_FILE_NAME, null);

        List<String> vl = new ArrayList();

        for (Properties p : list) {
            String[] vs = StringUtils.split(p.getProperty(name), ",");
            for (String v : vs) {
                vl.add(StringUtils.trimToNull(v));
            }
        }

        return vl;
    }
}
