package chess;

import java.util.Collection;
import java.util.HashSet;

public class KingMoveFinder extends PieceMovesFinder {

    private Collection<ChessMove> validPositions = new HashSet<>();
    private final ChessGame.TeamColor currentTeam;
    private final ChessPosition myPosition;
    private final ChessBoard board;

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
        super();
        this.board = board;
        this.myPosition = myPosition;
        this.currentTeam = currentTeam;
    }

    public Collection<ChessMove> findKingMoves() {
        for (int[] direction : directions) {
            ChessPosition newPosition = new ChessPosition(
                myPosition.getRow() + direction[0],
                myPosition.getColumn() + direction[1]
            );

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
        return validPositions;
    }

    public static void main(String[] args) {
        ChessBoard board = new ChessBoard();
        ChessPosition myPosition = new ChessPosition(8, 8);
        KingMoveFinder finder = new KingMoveFinder(board, myPosition, ChessGame.TeamColor.BLACK);
        Collection<ChessMove> validPostions = finder.findKingMoves();
        System.out.println(validPostions);
    }
}
