package chess.move_finders;

import java.util.Collection;
import java.util.HashSet;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

public class KnightMoveFinder extends SimpleMove {

    private Collection<ChessMove> moves = new HashSet<>();
    private int directions[][] =
            {{2, 1}, {1, 2}, {-1, 2}, {-2, 1}, {-2, -1}, {-1, -2}, {1, -2}, {2, -1},};

    public KnightMoveFinder(ChessBoard board, ChessPosition myPosition,
            ChessGame.TeamColor currentTeam) {
        super();
        findSimpleMoves(directions, board, myPosition, currentTeam, moves);
    }

    public Collection<ChessMove> getMoves() {
        return moves;
    }
}
