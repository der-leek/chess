package chess;

import java.util.Collection;
import java.util.HashSet;

import chess.ChessGame.TeamColor;

public class TeamPositions {
    private Collection<ChessPosition> whitePositions;
    private Collection<ChessPosition> blackPositions;

    public TeamPositions() {
        whitePositions = new HashSet<ChessPosition>();
        blackPositions = new HashSet<ChessPosition>();

        for (int i = 1; i <= 8; i ++) {
            whitePositions.add(new ChessPosition(1, i));
            whitePositions.add(new ChessPosition(2, i));
            blackPositions.add(new ChessPosition(7, i));
            blackPositions.add(new ChessPosition(8, i));
        }
    }

    public Collection<ChessPosition> getFriendlyPositions(ChessGame.TeamColor teamColor) {
        return (teamColor == TeamColor.WHITE ? whitePositions : blackPositions);
    }

    public Collection<ChessPosition> getEnemyPositions(ChessGame.TeamColor teamColor) {
        return (teamColor == TeamColor.WHITE ? blackPositions : whitePositions);
    }

    public void captureEnemy(ChessMove move, ChessGame.TeamColor teamColor) {
        ChessPosition endPosition = move.getEndPosition();
        getEnemyPositions(teamColor).remove(endPosition);
        getFriendlyPositions(teamColor).add(endPosition);
    }

    public void basicMove(ChessMove move, ChessGame.TeamColor teamColor) {
        getFriendlyPositions(teamColor).remove(move.getStartPosition());
        getFriendlyPositions(teamColor).add(move.getEndPosition());
    }
}
