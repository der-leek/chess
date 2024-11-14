package serializer;

import com.google.gson.*;
import java.lang.reflect.Type;

public class Serializer {
    private final Gson gson;

    public Serializer() {
        this.gson = new Gson();
    }

    public String toJson(Object obj) {
        return gson.toJson(obj);
    }

    public <T> T fromJson(String json, Class<T> classOfT) throws JsonSyntaxException {
        return gson.fromJson(json, classOfT);
    }

    public <T> T fromJson(String json, Type typeOfT) throws JsonSyntaxException {
        return gson.fromJson(json, typeOfT);
    }
}
