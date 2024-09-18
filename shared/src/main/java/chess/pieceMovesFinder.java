package chess;

/**
 * A class that contains common methods between all pieces
 * @param board
 * @param newPosition
 * @param currentTeam
 * @returns boolean
 */
public class pieceMovesFinder {
    public static boolean positionAvailable(ChessBoard board, ChessPosition newPosition, ChessGame.TeamColor currentTeam) {
        int row = newPosition.getRow();
        int col = newPosition.getColumn();

        if (row < 1 | row > 8 | col < 1 | col > 8) {
            return false;
        }

        // enemy piece is valid position but is the last valid position!!!
        ChessPiece targetPiece = board.getPiece(newPosition);
        if (targetPiece instanceof ChessPiece) {
            ChessGame.TeamColor targetPieceTeam = targetPiece.getTeamColor();
            if (targetPieceTeam == currentTeam) {
                return false;
            }
        }

        return true;
    }
}