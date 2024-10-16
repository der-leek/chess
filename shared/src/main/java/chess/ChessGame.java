package chess;

import java.util.Collection;
import java.util.HashSet;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter signature of the existing methods.
 */
public class ChessGame {

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
        if (piece == null) {
            return new HashSet<>();
        }

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
        ChessPosition startPosition = move.getStartPosition();
        ChessPiece piece = board.getPiece(startPosition);

        if (piece == null) {
            throw new InvalidMoveException("No piece at the start position.");
        }
        if (!validMoves(startPosition).contains(move)) {
            throw new InvalidMoveException("Invalid move for the piece.");
        }
        if (piece.getTeamColor() != getTeamTurn()) {
            throw new InvalidMoveException("It's not this team's turn.");
        }

        ChessPiece.PieceType promotionPiece = move.getPromotionPiece();
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
}
