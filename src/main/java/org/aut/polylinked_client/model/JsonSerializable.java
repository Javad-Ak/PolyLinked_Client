package org.aut.polylinked_client.model;


import io.jsonwebtoken.lang.Classes;
import org.aut.utils.exceptions.NotAcceptableException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;

public interface JsonSerializable {
    JSONObject toJson();

    static <T extends JsonSerializable> T fromJson(JSONObject jsonObject, Class<T> cls) throws NotAcceptableException {
        try {
            return Classes.getConstructor(cls, JSONObject.class).newInstance(jsonObject);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new NotAcceptableException("Json constructor Not Found.");
        }
    }
}
