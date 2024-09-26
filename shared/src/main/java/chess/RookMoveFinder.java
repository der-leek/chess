package chess;

import java.util.Collection;
import java.util.HashSet;

public class RookMoveFinder extends PieceMovesFinder {

    private Collection<ChessMove> validPositions = new HashSet<>();

    private int directions[][] = {
        {1, 0},
        {-1, 0},
        {0, 1},
        {0, -1}
    };

    public RookMoveFinder(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor currentTeam) {
        super(board, myPosition, currentTeam);
        findLoopedMoves(directions, validPositions);
    }

    public Collection<ChessMove> findRookMoves() {
        return validPositions;
    }
}
