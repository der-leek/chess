package chess.move_finders;

import java.util.Collection;
import chess.*;

public abstract class SimpleMove extends PositionChecker {

    public SimpleMove() {
        super();
    }

    public void findSimpleMoves(int[][] directions, ChessBoard board, ChessPosition myPosition,
            ChessGame.TeamColor currentTeam, Collection<ChessMove> validPositions) {

        for (int[] direction : directions) {
            ChessPosition newPosition = new ChessPosition(myPosition.getRow() + direction[0],
                    myPosition.getColumn() + direction[1]);

            if (!newPosition.inBounds()) {
                continue;
            }

            ChessMove move = new ChessMove(myPosition, newPosition, null);
            ChessPiece targetPiece = board.getPiece(newPosition);
            if (isNull(targetPiece)) {
                validPositions.add(move);
            } else if (isEnemyPosition(targetPiece, currentTeam)) {
                validPositions.add(move);
            } else { // is friendly position
                continue;
            }
        }
    }
}
