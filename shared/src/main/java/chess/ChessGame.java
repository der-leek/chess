package chess;

import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard board = new ChessBoard();
    private TeamColor teamTurn;
    private TeamPositions teamPositions;

    public ChessGame() {
        board.resetBoard();
        setTeamTurn(TeamColor.WHITE);
        teamPositions = new TeamPositions();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK;

        private TeamColor toggle() {
            return this == WHITE ? BLACK : WHITE;
        }
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        Collection<ChessMove> moves = piece.pieceMoves(board, startPosition);

        // ChessGame gameClone = clone();
        // for (ChessMove move : moves) {
        //     ChessPiece pieceClone = board.getPiece(move.getStartPosition());
        //     gameClone.board.addPiece(move.getEndPosition(), pieceClone);
        //     gameClone.board.removePiece(move.getStartPosition());

        //     if (isInCheck(gameClone.teamTurn)) {
        //         moves.remove(move);
        //     }
        // }

        return moves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece.PieceType promotionPiece = move.getPromotionPiece();
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();
        ChessPiece piece = board.getPiece(startPosition);

        if (piece == null) {
            throw new InvalidMoveException();
        }

        boolean invalidMove = (!validMoves(startPosition).contains(move));
        boolean wrongTurn = (piece.getTeamColor() != getTeamTurn());
        if (invalidMove | wrongTurn) {
            throw new InvalidMoveException();
        }

        if (promotionPiece != null) {
            piece = new ChessPiece(getTeamTurn(), promotionPiece);
        }

        board.addPiece(endPosition, piece);
        board.removePiece(startPosition);
        setTeamTurn(getTeamTurn().toggle());
        // update position sets
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        // START HERE

        // get king position
        // for every move
            // if endPosition is the same as the king's
                // return true
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        // clone board
        // for (ChessPosition position : friendlyPositions)
            // for (ChessMove move : validMoves(posiiton))
                // make move
                // if not in check
                    // return false
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheckmate(teamColor)) {
            return false;
        }

        for (ChessPosition position : teamPositions.getFriendlyPositions(teamColor)) {
            if (validMoves(position) != null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
