package server;

import java.net.URI;
import java.util.Map;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;

public class HTTP {

    private final String urlString;

    public HTTP(int port) {
        urlString = "http://localhost:" + port + "/";
    }

    public Map<String, String> delete(String endpoint) throws URISyntaxException, IOException {
        URI uri = new URI(urlString + endpoint);
        HttpURLConnection http = (HttpURLConnection) uri.toURL().openConnection();
        http.setReadTimeout(5000);
        http.setRequestMethod("DELETE");
        http.addRequestProperty("Content-Type", "application/json");
        http.connect();

        return receiveResponse(http);
    }

    private Map<String, String> receiveResponse(HttpURLConnection http) throws IOException {
        String statusCode = Integer.toString(http.getResponseCode());
        String statusMessage = http.getResponseMessage();
        String responseBody = readResponseBody(http);

        var response = Map.of("statusCode", statusCode, "statusMessage", statusMessage, "body",
                responseBody);

        return response;
    }

    private String readResponseBody(HttpURLConnection http) throws IOException {
        String responseBody = "";

        try (InputStream respBody =
                (http.getResponseCode() == HttpURLConnection.HTTP_OK) ? http.getInputStream()
                        : http.getErrorStream()) {
            InputStreamReader inputStreamReader = new InputStreamReader(respBody);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            responseBody = reader.readLine();
        }

        return responseBody;
    }
}
