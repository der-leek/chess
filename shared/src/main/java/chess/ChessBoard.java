package chess;

import java.util.Arrays;
import java.util.Map;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter signature of the existing methods.
 */
public class ChessBoard implements Cloneable {
    private final int boardLength = 8;
    private ChessPiece squares[][];

    public ChessBoard() {
        squares = new ChessPiece[boardLength][boardLength];
    }

    /**
     * Adds a chess piece to the chessboard Adds that piece's position to its teams Set of positions
     *
     * @param position where to add the piece to
     * @param piece the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Removes a piece from the chessboard
     *
     * @param position whice position to remove
     */
    public void removePiece(ChessPosition position) {
        squares[position.getRow() - 1][position.getColumn() - 1] = null;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow() - 1][position.getColumn() - 1];
    }

    /**
     * Sets the board to the default starting board (How the game of chess normally starts)
     */
    public void resetBoard() {
        squares = new ChessPiece[boardLength][boardLength];

        Map<Integer, ChessPiece.PieceType> boardStructure =
                Map.of(1, ChessPiece.PieceType.ROOK, 2, ChessPiece.PieceType.KNIGHT, 3,
                        ChessPiece.PieceType.BISHOP, 4, ChessPiece.PieceType.QUEEN, 5,
                        ChessPiece.PieceType.KING, 6, ChessPiece.PieceType.BISHOP, 7,
                        ChessPiece.PieceType.KNIGHT, 8, ChessPiece.PieceType.ROOK);

        for (Map.Entry<Integer, ChessPiece.PieceType> entry : boardStructure.entrySet()) {
            int col = entry.getKey();

            addPiece(new ChessPosition(1, col),
                    new ChessPiece(ChessGame.TeamColor.WHITE, entry.getValue()));
            addPiece(new ChessPosition(2, col),
                    new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
            addPiece(new ChessPosition(7, col),
                    new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
            addPiece(new ChessPosition(8, col),
                    new ChessPiece(ChessGame.TeamColor.BLACK, entry.getValue()));
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.deepHashCode(squares);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        ChessBoard other = (ChessBoard) obj;
        if (!Arrays.deepEquals(squares, other.squares)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder boardStringBuilder = new StringBuilder();
        String pipe = "|";
        String space = " ";

        for (int i = boardLength - 1; i >= 0; i--) {
            boardStringBuilder.append(pipe);
            for (ChessPiece piece : squares[i]) {
                String pieceString = (piece != null ? piece.toString() : space);
                boardStringBuilder.append(pieceString).append(pipe);
            }
            boardStringBuilder.append("\n");
        }
        return boardStringBuilder.toString();
    }

    @Override
    public ChessBoard clone() {
        try {
            ChessBoard boardClone = (ChessBoard) super.clone();
            boardClone.squares = new ChessPiece[boardLength][boardLength];

            for (int i = 0; i < boardLength; i++) {
                for (int j = 0; j < boardLength; j++) {
                    if (this.squares[i][j] != null) {
                        boardClone.squares[i][j] = this.squares[i][j].clone();
                    }
                }
            }
            return boardClone;
        } catch (CloneNotSupportedException ex) {
            throw new RuntimeException(ex);
        }
    }
}
