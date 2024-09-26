package chess;

import java.util.Collection;

public class PieceMovesFinder {
    private final ChessGame.TeamColor currentTeam;
    private final ChessPosition myPosition;
    private final ChessBoard board;

    public PieceMovesFinder(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor currentTeam) {
        super();
        this.board = board;
        this.myPosition = myPosition;
        this.currentTeam = currentTeam;
    }

    public boolean isNull(ChessPiece targetPiece) {
        if (targetPiece == null) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isEnemyPosition(ChessPiece targetPiece, ChessGame.TeamColor currentTeam) {
        ChessGame.TeamColor targetPieceTeam = targetPiece.getTeamColor();
        if (targetPieceTeam != currentTeam) {
            return true;
        } else {
            return false;
        }
    }

    public void findSimpleMoves(int[][] directions, Collection<ChessMove> validPositions) {
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
    }

    public void findLoopedMoves(int[][] directions, Collection<ChessMove> validPositions) {
        for (int[] direction : directions) {
            ChessPosition newPosition = new ChessPosition(
                myPosition.getRow() + direction[0],
                myPosition.getColumn() + direction[1]
            );

            while (newPosition.inBounds()) {
                ChessMove move = new ChessMove(myPosition, newPosition, null);
                ChessPiece targetPiece = board.getPiece(newPosition);

                if (isNull(targetPiece)) {
                    validPositions.add(move);
                    newPosition = new ChessPosition(
                        newPosition.getRow() + direction[0],
                        newPosition.getColumn() + direction[1]
                    );
                } else if (isEnemyPosition(targetPiece, currentTeam)) {
                    validPositions.add(move);
                    break;
                } else { // is friendly position
                    break;
                }
            }
        }
    }
}