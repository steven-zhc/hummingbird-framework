package com.hczhang.hummingbird.serializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.Date;

/**
 * Created by steven on 1/28/15.
 */
public class DateDeserializer implements JsonDeserializer<Date> {

    private static Logger logger = LoggerFactory.getLogger(DateDeserializer.class);

    static final DateTimeFormatter df1 = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");
    static final DateTimeFormatter df2 = DateTimeFormat.forPattern("MMM d, yyyy h:mm:ss a");

    @Override
    public Date deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        String myDate = jsonElement.getAsString();

        if (StringUtils.contains(myDate, "T")) {

            // yyyy-MM-dd'T'HH:mm:ss
            return df1.parseDateTime(myDate).toDate();
        } else {

            // MMM d, yyyy hh:mm:ss a    Jun 1, 1989 12:00:00 AM
            return df2.parseDateTime(myDate).toDate();
        }

    }

}
