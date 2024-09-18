package chess;

import java.util.Collection;
import java.util.ArrayList;

public class BishopMoveFinder extends PieceMovesFinder {

    private Collection<ChessMove> validPositions = new ArrayList<>();
    private final ChessGame.TeamColor currentTeam;
    private final ChessPosition myPosition;
    private final ChessBoard board;

    private int directions[][] = {
        {1, 1},
        {1, -1},
        {-1, -1},
        {-1, 1}
    };

    public BishopMoveFinder(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor currentTeam) {
        super();
        this.board = board;
        this.myPosition = myPosition;
        this.currentTeam = currentTeam;
    }

    public Collection<ChessMove> findBishopMoves() {
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

    public static void main(String[] args) {
        ChessBoard board = new ChessBoard();
        ChessPosition myPosition = new ChessPosition(2, 3);
        BishopMoveFinder finder = new BishopMoveFinder(board, myPosition, ChessGame.TeamColor.BLACK);
        Collection<ChessMove> validPostions = finder.findBishopMoves();
        System.out.println(validPostions);
    }
}
