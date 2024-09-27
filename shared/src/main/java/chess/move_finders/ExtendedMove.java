package chess.move_finders;

import java.util.Collection;
import chess.*;

public class ExtendedMove extends PositionChecker {

    public ExtendedMove() {
        super();
    }

    public void findLoopedMoves(
        int[][] directions,
        Collection<ChessMove> validPositions,
        ChessBoard board,
        ChessPosition myPosition,
        ChessGame.TeamColor currentTeam
        ) {

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
    }
}
