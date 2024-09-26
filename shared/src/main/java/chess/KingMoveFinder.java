package chess;

import java.util.Collection;
import java.util.HashSet;

public class KingMoveFinder extends PieceMovesFinder {

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

    public KingMoveFinder(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor currentTeam) {
        super(board, myPosition, currentTeam);
        findSimpleMoves(directions, validPositions);
    }

    public Collection<ChessMove> findKingMoves() {
        return validPositions;
    }
}
