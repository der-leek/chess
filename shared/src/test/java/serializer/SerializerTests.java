package serializer;

import responses.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class SerializerTests {

    private final Serializer serializer = new Serializer();

    @Test
    public void serializeErrorResponse() {
        String message = "/db has no request body";
        var res = new ErrorResponse(message);
        Assertions.assertEquals("{\"message\":\"/db has no request body\"}",
                serializer.toJson(res));
    }

    @Test
    public void deserializeErrorResponse() {
        var response = new ErrorResponse("/db has no request body");
        String message = "{\"message\":\"/db has no request body\"}";
        Assertions.assertEquals(serializer.fromJson(message, ErrorResponse.class), response);
    }
}
