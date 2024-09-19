package chess;

public class PieceMovesFinder {
    public PieceMovesFinder() {}

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
}