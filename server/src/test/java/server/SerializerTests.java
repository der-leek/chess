package server;

import requests_results.ClearResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class SerializerTests {

    @Test
    public void serializeClearResponse() {
        String message = "/db has no request body";
        var res = new ClearResponse(message);
        var gson = new Serializer<ClearResponse>();
        Assertions.assertEquals("{\"message\":\"/db has no request body\"}", gson.toJson(res));
    }

    @Test
    public void deserializeClearResponse() {
        var response = new ClearResponse("/db has no request body");
        var gson = new Serializer<ClearResponse>();
        String message = "{\"message\":\"/db has no request body\"}";
        Assertions.assertEquals(gson.fromJson(message, ClearResponse.class), response);
    }
}
