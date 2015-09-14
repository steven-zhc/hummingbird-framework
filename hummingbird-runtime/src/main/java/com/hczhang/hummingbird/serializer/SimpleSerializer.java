package com.hczhang.hummingbird.serializer;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * Created by steven on 4/16/14.
 */
public class SimpleSerializer implements Serializer {

    private static Logger logger = LoggerFactory.getLogger(SimpleSerializer.class);

    @Override
    public byte[] serialize(Object object) {
        Validate.notNull(object, "Object is null");

        byte[] blob = null;
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bytes);
            out.writeObject(object);

            blob = bytes.toByteArray();

            bytes.close();
            out.close();

        } catch (IOException e) {
            logger.error("Serialize throw IOException.", e);
        }

        return blob;
    }

    @Override
    public <T> T deserialize(byte[] blob, Class<T> tClass) {

        Validate.notNull(blob, "Data content is null");
        Validate.notNull(tClass, "Class is null");

        T instance = null;
        try {
            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(blob));
            instance = (T) in.readObject();

        } catch (IOException e) {
            logger.error("De-serialize throw IOException.", e);
        } catch (ClassNotFoundException e) {
            logger.error("De-serialize throw ClassNotFoundException.", e);
        }

        return instance;
    }
}
