package chess;

import java.util.Collection;
import java.util.HashSet;

import chess.ChessGame.TeamColor;

public class TeamPositions {
    private Collection<ChessPosition> whitePositions;
    private Collection<ChessPosition> blackPositions;
    private ChessPosition whiteKingPosition;
    private ChessPosition blackKingPosition;

    public TeamPositions(ChessBoard board) {
        refreshPositions(board);
    }

    public void refreshPositions(ChessBoard board) {
        whitePositions = new HashSet<ChessPosition>();
        blackPositions = new HashSet<ChessPosition>();
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(position);
                if (piece == null) {
                    continue;
                }

                if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                    whitePositions.add(position);
                } else {
                    blackPositions.add(position);
                }

                if (piece.getPieceType() == ChessPiece.PieceType.KING) {
                    setKingPosition(piece.getTeamColor(), position);
                }
            }
        }
    }

    public ChessPosition getKingPosition(ChessGame.TeamColor teamColor) {
        return (teamColor == ChessGame.TeamColor.WHITE ? whiteKingPosition : blackKingPosition);
    }

    public void setKingPosition(ChessGame.TeamColor teamColor, ChessPosition kingPosition) {
        if (teamColor == ChessGame.TeamColor.WHITE) {
            this.whiteKingPosition = kingPosition;
        } else {
            this.blackKingPosition = kingPosition;
        }
    }

    public Collection<ChessPosition> getFriendlyPositions(ChessGame.TeamColor teamColor) {
        return (teamColor == TeamColor.WHITE ? whitePositions : blackPositions);
    }

    public Collection<ChessPosition> getEnemyPositions(ChessGame.TeamColor teamColor) {
        return (teamColor == TeamColor.WHITE ? blackPositions : whitePositions);
    }
}
