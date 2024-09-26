package chess;

import java.util.Collection;
import java.util.HashSet;

public class KnightMoveFinder extends PieceMovesFinder {

    private Collection<ChessMove> validPositions = new HashSet<>();

    private int directions[][] = {
        {2, 1},
        {1, 2},
        {-1, 2},
        {-2, 1},
        {-2, -1},
        {-1, -2},
        {1, -2},
        {2, -1},
    };

    public KnightMoveFinder(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor currentTeam) {
        super(board, myPosition, currentTeam);
        findSimpleMoves(directions, validPositions);
    }

    public Collection<ChessMove> findKnightMoves() {
        return validPositions;
    }
}
