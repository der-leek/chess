package chess;
import java.util.HashMap;
import java.util.Map;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private ChessPiece squares[][] = new ChessPiece[8][8];

    public ChessBoard() {
        resetBoard();
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow()][position.getColumn()] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow()][position.getColumn()];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        Map<Integer, ChessPiece.PieceType> boardStructure = new HashMap<>();
        boardStructure.put(0, ChessPiece.PieceType.ROOK);
        boardStructure.put(1, ChessPiece.PieceType.KNIGHT);
        boardStructure.put(2, ChessPiece.PieceType.BISHOP);
        boardStructure.put(3, ChessPiece.PieceType.QUEEN);
        boardStructure.put(4, ChessPiece.PieceType.KING);
        boardStructure.put(5, ChessPiece.PieceType.BISHOP);
        boardStructure.put(6, ChessPiece.PieceType.KNIGHT);
        boardStructure.put(7, ChessPiece.PieceType.ROOK);

        for (Map.Entry<Integer, ChessPiece.PieceType> entry : boardStructure.entrySet()) {
            int col = entry.getKey();

            addPiece(
                new ChessPosition(0, col), 
                new ChessPiece(ChessGame.TeamColor.BLACK, entry.getValue())
            );

            addPiece(
                new ChessPosition(1, col), 
                new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN)
            );
            
            addPiece(
                new ChessPosition(6, col), 
                new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN)
                );  

            addPiece(
                new ChessPosition(7, col), 
                new ChessPiece(ChessGame.TeamColor.WHITE, entry.getValue())
                );       
        }            
    }
}
