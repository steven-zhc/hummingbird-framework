package com.hczhang.hummingbird.serializer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;

/**
 * Created by steven on 4/17/14.
 */
public class JsonSerializer implements Serializer {

    private Gson gson;

    public JsonSerializer() {
        this.gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .registerTypeAdapter(Date.class, new DateDeserializer())
                .create();
    }

    @Override
    public byte[] serialize(Object object) {
        String json = gson.toJson(object);
        return json.getBytes();
    }

    @Override
    public <T> T deserialize(byte[] blob, Class<T> tClass) {
        String json = new String(blob);
        return gson.fromJson(json, tClass);
    }

}
