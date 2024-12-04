package websocket.commands;

import chess.ChessGame;

public class ConnectCommand extends UserGameCommand {

    private final ChessGame.TeamColor teamColor;

    public ConnectCommand(String username, String authToken, Integer gameID,
            ChessGame.TeamColor teamColor) {
        super(UserGameCommand.CommandType.CONNECT, username, authToken, gameID);
        this.teamColor = teamColor;
    }

    public ConnectCommand(String username, String authToken, Integer gameID) {
        super(UserGameCommand.CommandType.CONNECT, username, authToken, gameID);
        teamColor = null;
    }

    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }
}
