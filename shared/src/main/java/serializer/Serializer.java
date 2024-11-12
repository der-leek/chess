package serializer;

import com.google.gson.*;

public class Serializer<T> {
    private final Gson gson;

    public Serializer() {
        this.gson = new Gson();
    }

    public String toJson(T obj) {
        return gson.toJson(obj);
    }

    public T fromJson(String json, Class<T> clazz) throws JsonSyntaxException {
        return gson.fromJson(json, clazz);
    }
}