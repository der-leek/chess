package serializer;

import com.google.gson.*;
import java.lang.reflect.Type;
import websocket.messages.*;

public class Serializer {
    public static class ServerMessageDeserializer implements JsonDeserializer<ServerMessage> {
        @Override
        public ServerMessage deserialize(JsonElement json, Type typeOfT,
                JsonDeserializationContext context) {
            var jsonObject = json.getAsJsonObject();
            var type = jsonObject.get("serverMessageType").getAsString();

            switch (type) {
                case ("ERROR"):
                    return context.deserialize(jsonObject, ErrorMessage.class);
                case ("LOAD_GAME"):
                    return context.deserialize(jsonObject, LoadGameMessage.class);
                case ("NOTIFICATION"):
                    return context.deserialize(jsonObject, NotificationMessage.class);
                default:
                    return null;
            }
        }
    }

    private final Gson gson;

    public Serializer() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(ServerMessage.class, new ServerMessageDeserializer()).create();
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
