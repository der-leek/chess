package chess;

import java.util.Collection;
import java.util.HashSet;

public class QueenMoveFinder extends PieceMovesFinder {

    private Collection<ChessMove> validPositions = new HashSet<>();

    private int directions[][] = {
        {1, 0},
        {1, 1},
        {0, 1},
        {-1, 1},
        {-1, 0},
        {-1, -1},
        {0, -1},
        {1, -1},
    };

    public QueenMoveFinder(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor currentTeam) {
        super(board, myPosition, currentTeam);
        findLoopedMoves(directions, validPositions);
    }

    public Collection<ChessMove> findQueenMoves() {
        return validPositions;
    }
}
