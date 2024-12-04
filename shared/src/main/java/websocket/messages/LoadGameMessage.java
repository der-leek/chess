package websocket.messages;

import chess.ChessGame;

public class LoadGameMessage extends ServerMessage {

    private final ChessGame game;
    private final ChessGame.TeamColor teamColor;

    public LoadGameMessage(ChessGame game, ChessGame.TeamColor teamColor) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
        this.teamColor = teamColor;
    }

    public ChessGame getGame() {
        return game;
    }

    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }
}
