package model;

import chess.*;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName,
        ChessGame game) {

    public GameData updateUsername(ChessGame.TeamColor playerColor, String username) {
        if (playerColor == ChessGame.TeamColor.WHITE) {
            return new GameData(gameID, username, blackUsername, gameName, game);
        } else {
            return new GameData(gameID, whiteUsername, username, gameName, game);
        }
    }
}
