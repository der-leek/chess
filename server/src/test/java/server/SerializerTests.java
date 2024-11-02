package server;

import responses.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class SerializerTests {

    @Test
    public void serializeErrorResponse() {
        String message = "/db has no request body";
        var res = new ErrorResponse(message);
        var gson = new Serializer<ErrorResponse>();
        Assertions.assertEquals("{\"message\":\"/db has no request body\"}", gson.toJson(res));
    }

    @Test
    public void deserializeErrorResponse() {
        var response = new ErrorResponse("/db has no request body");
        var gson = new Serializer<ErrorResponse>();
        String message = "{\"message\":\"/db has no request body\"}";
        Assertions.assertEquals(gson.fromJson(message, ErrorResponse.class), response);
    }
}
