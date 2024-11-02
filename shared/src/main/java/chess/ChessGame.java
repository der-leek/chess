package chess;

import java.util.Collection;
import java.util.HashSet;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter signature of the existing methods.
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
        WHITE, BLACK;

        private TeamColor toggle() {
            return this == WHITE ? BLACK : WHITE;
        }
    }

    /**
     * Executes a move on the board.
     *
     * @param board The chess board.
     * @param piece The piece to move.
     * @param move The move to execute.
     */
    public void executeMove(ChessBoard board, ChessPiece piece, ChessMove move) {
        board.addPiece(move.getEndPosition(), piece);
        board.removePiece(move.getStartPosition());
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        Collection<ChessMove> moves = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> invalidMoves = new HashSet<ChessMove>();

        for (ChessMove move : moves) {
            ChessBoard boardClone = board.clone();
            ChessPiece pieceClone = boardClone.getPiece(move.getStartPosition());
            executeMove(boardClone, pieceClone, move);
            TeamPositions clonedPositions = new TeamPositions(boardClone);

            ChessPosition kingPosition = clonedPositions.getKingPosition(pieceClone.getTeamColor());
            Collection<ChessPosition> enemyPositions =
                    clonedPositions.getEnemyPositions(pieceClone.getTeamColor());

            for (ChessPosition position : enemyPositions) {
                ChessPiece enemyPiece = boardClone.getPiece(position);
                for (ChessMove enemyMove : enemyPiece.pieceMoves(boardClone, position)) {
                    if (enemyMove.getEndPosition().equals(kingPosition)) {
                        invalidMoves.add(move);
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

        executeMove(board, piece, move);
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
        return isInCheck(teamColor, teamPositions);
    }

    /**
     * Determines if the given team is in check using the provided TeamPositions.
     *
     * @param teamColor The team color to check.
     * @param positions The TeamPositions to use for checking.
     * @return True if the specified team is in check, false otherwise.
     */
    public boolean isInCheck(TeamColor teamColor, TeamPositions positions) {
        ChessPosition kingPosition = positions.getKingPosition(teamColor);
        for (ChessPosition position : positions.getEnemyPositions(teamColor)) {
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
        if (!isInCheck(teamColor)) {
            return false;
        }

        for (ChessPosition position : teamPositions.getFriendlyPositions(teamColor)) {
            ChessBoard boardClone = board.clone();
            ChessPiece pieceClone = boardClone.getPiece(position);

            for (ChessMove move : validMoves(position)) {
                executeMove(boardClone, pieceClone, move);
                TeamPositions clonePositions = new TeamPositions(boardClone);
                if (!isInCheck(teamColor, clonePositions)) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }

        for (ChessPosition position : teamPositions.getFriendlyPositions(teamColor)) {
            if (!validMoves(position).isEmpty()) {
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((board == null) ? 0 : board.hashCode());
        result = prime * result + ((teamTurn == null) ? 0 : teamTurn.hashCode());
        result = prime * result + ((teamPositions == null) ? 0 : teamPositions.hashCode());
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
        ChessGame other = (ChessGame) obj;
        if (board == null) {
            if (other.board != null) {
                return false;
            }
        } else if (!board.equals(other.board)) {
            return false;
        }
        if (teamTurn != other.teamTurn) {
            return false;
        }
        if (teamPositions == null) {
            if (other.teamPositions != null) {
                return false;
            }
        } else if (!teamPositions.equals(other.teamPositions)) {
            return false;
        }
        return true;
    }
}
