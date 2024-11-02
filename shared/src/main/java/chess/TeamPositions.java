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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((whitePositions == null) ? 0 : whitePositions.hashCode());
        result = prime * result + ((blackPositions == null) ? 0 : blackPositions.hashCode());
        result = prime * result + ((whiteKingPosition == null) ? 0 : whiteKingPosition.hashCode());
        result = prime * result + ((blackKingPosition == null) ? 0 : blackKingPosition.hashCode());
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
        TeamPositions other = (TeamPositions) obj;
        if (whitePositions == null) {
            if (other.whitePositions != null) {
                return false;
            }
        } else if (!whitePositions.containsAll(other.whitePositions)) {
            return false;
        }
        if (blackPositions == null) {
            if (other.blackPositions != null) {
                return false;
            }
        } else if (!blackPositions.containsAll(other.blackPositions)) {
            return false;
        }
        if (whiteKingPosition == null) {
            if (other.whiteKingPosition != null) {
                return false;
            }
        } else if (!whiteKingPosition.equals(other.whiteKingPosition)) {
            return false;
        }
        if (blackKingPosition == null) {
            if (other.blackKingPosition != null) {
                return false;
            }
        } else if (!blackKingPosition.equals(other.blackKingPosition)) {
            return false;
        }
        return true;
    }
}
