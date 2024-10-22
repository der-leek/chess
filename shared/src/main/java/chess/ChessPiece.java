package chess;

import chess.finders.*;
import java.util.Collection;
import java.util.Map;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter signature of the existing methods.
 */
public class ChessPiece implements Cloneable {
    private final ChessGame.TeamColor pieceColor;
    private final ChessPiece.PieceType type;
    private final Map<PieceType, String> pieces = Map.of(PieceType.KING, "K", PieceType.QUEEN, "Q",
            PieceType.BISHOP, "B", PieceType.KNIGHT, "N", PieceType.ROOK, "R", PieceType.PAWN, "P");

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING, QUEEN, BISHOP, KNIGHT, ROOK, PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to Does not take into account moves that
     * are illegal due to leaving the king in danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        switch (type) {
            case BISHOP:
                BishopMoveFinder bishopFinder = new BishopMoveFinder(board, myPosition, pieceColor);
                return bishopFinder.getMoves();
            case ROOK:
                RookMoveFinder rookFinder = new RookMoveFinder(board, myPosition, pieceColor);
                return rookFinder.getMoves();
            case QUEEN:
                QueenMoveFinder queenFinder = new QueenMoveFinder(board, myPosition, pieceColor);
                return queenFinder.getMoves();
            case KING:
                KingMoveFinder kingFinder = new KingMoveFinder(board, myPosition, pieceColor);
                return kingFinder.getMoves();
            case KNIGHT:
                KnightMoveFinder knightFinder = new KnightMoveFinder(board, myPosition, pieceColor);
                return knightFinder.getMoves();
            case PAWN:
                PawnMoveFinder pawnFinder = new PawnMoveFinder(board, myPosition, pieceColor);
                return pawnFinder.getMoves();
            default:
                return null;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((pieceColor == null) ? 0 : pieceColor.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
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

        ChessPiece other = (ChessPiece) obj;
        if (pieceColor != other.pieceColor) {
            return false;
        }

        if (type != other.type) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        if (pieceColor == ChessGame.TeamColor.BLACK) {
            return pieces.get(type).toLowerCase();
        } else {
            return pieces.get(type);
        }
    }

    @Override
    public ChessPiece clone() {
        try {
            return (ChessPiece) super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new RuntimeException(ex);
        }
    }
}
