package chess;

import java.util.Collection;
import java.util.HashSet;

public class RookMoveFinder extends PieceMovesFinder {

    private Collection<ChessMove> validPositions = new HashSet<>();
    private final ChessGame.TeamColor currentTeam;
    private final ChessPosition myPosition;
    private final ChessBoard board;

    private int directions[][] = {
        {1, 0},
        {-1, 0},
        {0, 1},
        {0, -1}
    };

    public RookMoveFinder(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor currentTeam) {
        super();
        this.board = board;
        this.myPosition = myPosition;
        this.currentTeam = currentTeam;
    }

    public Collection<ChessMove> findRookMoves() {
        for (int[] direction : directions) {
            ChessPosition newPosition = new ChessPosition(
                myPosition.getRow() + direction[0],
                myPosition.getColumn() + direction[1]
            );

            while (newPosition.inBounds()) {
                ChessMove move = new ChessMove(myPosition, newPosition, null);
                ChessPiece targetPiece = board.getPiece(newPosition);

                if (isNull(targetPiece)) {
                    validPositions.add(move);
                    newPosition = new ChessPosition(
                        newPosition.getRow() + direction[0],
                        newPosition.getColumn() + direction[1]
                    );
                } else if (isEnemyPosition(targetPiece, currentTeam)) {
                    validPositions.add(move);
                    break;
                } else { // is friendly position
                    break;
                }
            }
        }
        return validPositions;
    }
}
