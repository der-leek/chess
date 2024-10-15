package chess.move_finders;

import java.util.Collection;
import java.util.HashSet;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

public class KingMoveFinder extends SimpleMove {

    private Collection<ChessMove> moves = new HashSet<>();
    private int directions[][] =
            {{1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}, {1, -1},};

    public KingMoveFinder(ChessBoard board, ChessPosition myPosition,
            ChessGame.TeamColor currentTeam) {
        super();
        findSimpleMoves(directions, board, myPosition, currentTeam, moves);
    }

    public Collection<ChessMove> getMoves() {
        return moves;
    }
}
