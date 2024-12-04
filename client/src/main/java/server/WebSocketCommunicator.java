package server;

import serializer.*;
import javax.websocket.*;
import websocket.messages.*;
import java.net.URI;
import websocket.commands.UserGameCommand;

public class WebSocketCommunicator extends Endpoint {
    public Session session;
    private Serializer serializer = new Serializer();

    public WebSocketCommunicator(int port, ServerMessageObserver observer) throws Exception {
        URI uri = new URI("ws://localhost:" + port + "/ws");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            public void onMessage(String message) {
                try {
                    var serverMessage = serializer.fromJson(message, ServerMessage.class);
                    observer.notify(serverMessage);
                } catch (Exception ex) {
                    observer.notify(new ErrorMessage(ex.getMessage()));
                }
            }
        });
    }

    public void send(UserGameCommand command) throws Exception {
        this.session.getBasicRemote().sendText(serializer.toJson(command));
    }

    public void onOpen(Session session, EndpointConfig endpointConfig) {}
}
