package chess;

import java.util.Collection;
import java.util.HashSet;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame implements Cloneable {

    private ChessBoard board;
    private TeamColor teamTurn;
    private TeamPositions teamPositions;

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        teamPositions = new TeamPositions(board);
        setTeamTurn(TeamColor.WHITE);
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
        Collection<ChessMove> invalidMoves = new HashSet<ChessMove>();

        ChessBoard boardClone;
        for (ChessMove move : moves) {
            try {
                boardClone = board.clone();
            } catch (CloneNotSupportedException ex) {
                System.out.println(ex);
                continue;
            }

            TeamPositions clonedPositions = new TeamPositions(boardClone);
            ChessPiece pieceClone = boardClone.getPiece(move.getStartPosition());
            boardClone.addPiece(move.getEndPosition(), pieceClone);
            boardClone.removePiece(move.getStartPosition());

            ChessPosition kingPosition = clonedPositions.getKingPosition(teamTurn);
            for (ChessPosition position : clonedPositions.getEnemyPositions(teamTurn)) {
                ChessPiece enemyPiece = boardClone.getPiece(position);
                if (enemyPiece != null) { // FIXME: there shouldn't be any null pieces in enemyPositions
                    for (ChessMove enemyMove : enemyPiece.pieceMoves(boardClone, position)) {
                        if (enemyMove.getEndPosition().equals(kingPosition)) {
                            invalidMoves.add(move);
                        }
                    }
                }
            }
        }

        moves.removeAll(invalidMoves);
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
        teamPositions.refreshPositions(board);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = teamPositions.getKingPosition(teamColor);
        for (ChessPosition position : teamPositions.getEnemyPositions(teamColor)) {
            for (ChessMove move : validMoves(position)) {
                if (move.getEndPosition().equals(kingPosition)) {
                    return true;
                }
            }
        }
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
        if (isInCheck(teamColor)) {
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
        teamPositions = new TeamPositions(board);
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
