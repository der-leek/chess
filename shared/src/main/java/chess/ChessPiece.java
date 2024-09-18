package chess;

import java.util.Collection;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final ChessGame.TeamColor pieceColor;
    private final ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
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
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        switch (type) {
            case BISHOP:
                BishopMoveFinder bishopFinder = new BishopMoveFinder(board, myPosition, pieceColor);
                Collection<ChessMove> bishopPositions = bishopFinder.findBishopMoves();
                return bishopPositions;
            case ROOK:
                RookMoveFinder rookFinder = new RookMoveFinder(board, myPosition, pieceColor);
                Collection<ChessMove> rookPositions = rookFinder.findRookMoves();
                return rookPositions;
            case QUEEN:
                QueenMoveFinder queenFinder = new QueenMoveFinder(board, myPosition, pieceColor);
                Collection<ChessMove> queenPositions = queenFinder.findQueenMoves();
                return queenPositions;
            case KING:
                KingMoveFinder kingFinder = new KingMoveFinder(board, myPosition, pieceColor);
                Collection<ChessMove> kingPositions = kingFinder.findKingMoves();
                return kingPositions;
            case KNIGHT:
                KnightMoveFinder knightFinder = new KnightMoveFinder(board, myPosition, pieceColor);
                Collection<ChessMove> knightPositions = knightFinder.findKnightMoves();
                return knightPositions;
            case PAWN:
                
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
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ChessPiece other = (ChessPiece) obj;
        if (pieceColor != other.pieceColor)
            return false;
        if (type != other.type)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "ChessPiece [pieceColor=" + pieceColor + ", type=" + type + "]";
    }
}
