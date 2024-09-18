package chess;

import java.util.Collection;
import java.util.HashSet;

public class PawnMoveFinder extends PieceMovesFinder {

    private Collection<ChessMove> validPositions = new HashSet<>();
    private final ChessGame.TeamColor currentTeam;
    private final ChessPosition myPosition;
    private final ChessBoard board;

    private int[] direction = {1, 0};
    private int multiplier = 1;
    private boolean canPromote = false;

    public PawnMoveFinder(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor currentTeam) {
        super();
        this.board = board;
        this.myPosition = myPosition;
        this.currentTeam = currentTeam;
        defineDirection(currentTeam);
        determineFirstMove(myPosition);
        determinePromotion(myPosition);
    }

    public Collection<ChessMove> findPawnMoves() {
        ChessPosition newPosition = new ChessPosition(
            myPosition.getRow() + (multiplier * direction[0]),
            myPosition.getColumn() + (multiplier * direction[1])
        );

        if (!newPosition.inBounds()) {
            return null;
        }

        ChessPiece targetPiece = board.getPiece(newPosition);

        if (isNull(targetPiece)) {
            validPositions.add(
                new ChessMove(myPosition, newPosition, null)
            );
        }

        if (isEnemyPosition(targetPiece, currentTeam)) {

            // validPositions.add(move);
        }

        // look at next position forward
            // if null
            // if enemy
                // add diagonals to validpos

        return null;
    }

    private void defineDirection(ChessGame.TeamColor currentTeam) {
        if (currentTeam == ChessGame.TeamColor.BLACK) {
            direction[0] = -1;
        }
    }

    private void determineFirstMove(ChessPosition myPosition) {
        // TODO: MAKE SURE BOTH SPACES AHEAD ARE UNOCCUPIED
        if (myPosition.getRow() == 2 | myPosition.getRow() == 7) {
            multiplier = 2;
        }
    }

    private void determinePromotion(ChessPosition myPosition) {
        if (myPosition.getRow() == 1 | myPosition.getRow() == 8) {
            canPromote = true;
        }
    }
}

// Pawn
    // every move
        // check the three squares in front
    // if next diagonal has an enemy piece (capture diagonally)
    // can promote if at the 1 or 8 (IMPLEMENT LATER)