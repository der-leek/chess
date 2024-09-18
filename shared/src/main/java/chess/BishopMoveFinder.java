package chess;

import java.util.Collection;
import java.util.ArrayList;

public class BishopMoveFinder extends pieceMovesFinder {

    private Collection<ChessMove> validPositions = new ArrayList<>();
    private int directions[][] = {
        {1, 1},
        {1, -1},
        {-1, -1},
        {-1, 1}
    };

    public BishopMoveFinder() {
        super();
    }

    /**
     * @param board
     * @param myPosition
     * @param currentTeam
     * @return validPositions
     */
    public Collection<ChessMove> findBishopMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor currentTeam) {

        for (int[] direction : directions) {
            ChessPosition newPosition = new ChessPosition(
                myPosition.getRow() + direction[0],
                myPosition.getColumn() + direction[1]
            );

            while (positionAvailable(board, newPosition, currentTeam)) {
                ChessMove move = new ChessMove(myPosition, newPosition, null);
                validPositions.add(move);
                newPosition = new ChessPosition(
                    newPosition.getRow() + direction[0],
                    newPosition.getColumn() + direction[1]
                );
            }
        }

        return validPositions;
    }

    public static void main(String[] args) {
        BishopMoveFinder finder = new BishopMoveFinder();
        ChessBoard board = new ChessBoard();
        ChessPosition myPosition = new ChessPosition(2, 3);
        Collection<ChessMove> validPostions = finder.findBishopMoves(board, myPosition, ChessGame.TeamColor.BLACK);
        System.out.println(validPostions);
    }

}

 // Bishop
    // can move any number of spaces diagonally