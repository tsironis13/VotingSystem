package com.votingsystem.tsiro.deserializer;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.votingsystem.tsiro.POJO.Firm;

import java.lang.reflect.Type;

/**
 * Created by user on 12/11/2015.
 */
public class FirmsDeserializer implements JsonDeserializer<Firm> {

    private static final String debugTag = "FirmsDeserializer";

    @Override
    public Firm deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

       try {
           JsonObject jsonObject = json.getAsJsonObject();
           return new Gson().fromJson(jsonObject.get("firms").getAsJsonObject(), Firm.class);
       } catch (JsonParseException jsonParseException) {
           jsonParseException.printStackTrace();
           return null;
       }
    }
}
