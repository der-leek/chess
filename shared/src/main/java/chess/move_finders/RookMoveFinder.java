package chess.move_finders;

import java.util.Collection;
import java.util.HashSet;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

public class RookMoveFinder extends ExtendedMove {

    private Collection<ChessMove> mvoes = new HashSet<>();
    private int directions[][] = {
        {1, 0},
        {-1, 0},
        {0, 1},
        {0, -1}
    };

    public RookMoveFinder(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor currentTeam) {
        super();
        findLoopedMoves(directions, mvoes, board, myPosition, currentTeam);
    }

    public Collection<ChessMove> getMoves() {
        return mvoes;
    }
}
