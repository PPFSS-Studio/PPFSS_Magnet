// PPFSS_Magnet Plugin 
// Авторские права (c) 2026 PPFSS
// Лицензия: MIT

package com.ppfss.libs.adapter;

import com.google.gson.*;
import com.ppfss.magnet.model.FilterType;

import java.lang.reflect.Type;

@GsonAdapter(FilterType.class)
public class FilterTypeAdapter implements JsonSerializer<FilterType>, JsonDeserializer<FilterType> {
    @Override
    public FilterType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonNull())return FilterType.BLACKLIST;

        String string = json.getAsString();

        return FilterType.of(string, FilterType.BLACKLIST);
    }

    @Override
    public JsonElement serialize(FilterType src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src == null ? "blacklist" : src.name().toLowerCase());
    }
}
