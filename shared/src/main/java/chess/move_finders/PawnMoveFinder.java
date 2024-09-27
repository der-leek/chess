package chess.move_finders;

import java.util.Collection;
import java.util.HashSet;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

public class PawnMoveFinder extends PositionChecker {
    private Collection<ChessMove> moves = new HashSet<>();
    private HashSet<int[]> directions = new HashSet<>();

    private final ChessGame.TeamColor currentTeam;
    private final ChessPosition myPosition;
    private final ChessBoard board;

    private int forward = 1;
    private boolean canPromote = false;
    private ChessPiece.PieceType[] promotionPieces = {
        ChessPiece.PieceType.QUEEN,
        ChessPiece.PieceType.KNIGHT,
        ChessPiece.PieceType.BISHOP,
        ChessPiece.PieceType.ROOK
    };

    public PawnMoveFinder(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor currentTeam) {
        super();
        this.board = board;
        this.myPosition = myPosition;
        this.currentTeam = currentTeam;
        defineForward(currentTeam);
        defineDirections(myPosition);
    }

    private void defineForward(ChessGame.TeamColor currentTeam) {
        if (currentTeam == ChessGame.TeamColor.BLACK) {
            forward = -1;
        }
    }

    private void defineDirections(ChessPosition myPosition) {
        int[] straight = {forward, 0};
        int[] diagLeft = {forward, -1};
        int[] diagRight = {forward, 1};

        directions.add(straight);
        directions.add(diagLeft);
        directions.add(diagRight);

        defineFirstMove(myPosition);
    }

    private void defineFirstMove(ChessPosition myPosition) {
        ChessPiece oneForward = board.getPiece(
            new ChessPosition(
                myPosition.getRow() + forward,
                myPosition.getColumn()
            )
        );
        boolean firstWhiteMove = (currentTeam == ChessGame.TeamColor.WHITE & myPosition.getRow() == 2);
        boolean firstBlackMove = (currentTeam == ChessGame.TeamColor.BLACK & myPosition.getRow() == 7);
        boolean firstMoveClear = (oneForward == null);
        if ((firstWhiteMove | firstBlackMove) & firstMoveClear) {
            int[] firstMove = {2*forward, 0};
            directions.add(firstMove);
        }
    }

    public Collection<ChessMove> getMoves() {
        for (int[] direction : directions) {
            ChessPosition newPosition = new ChessPosition(
                myPosition.getRow() + direction[0],
                myPosition.getColumn() + direction[1]
            );

            if (!newPosition.inBounds()) {
                continue;
            }

            ChessPiece targetPiece = board.getPiece(newPosition);
            determinePromotion(newPosition);

            boolean straightMove = (direction[1] == 0);
            boolean diagMove = (direction[1] == 1 | direction[1] == -1);
            if (isNull(targetPiece)) {
                if (straightMove) {
                    addMove(newPosition);
                }
            } else if (isEnemyPosition(targetPiece, currentTeam) & diagMove) {
                addMove(newPosition);
            }
        }
        return moves;
    }

    private void determinePromotion(ChessPosition newPosition) {
        if (newPosition.getRow() == 1 | newPosition.getRow() == 8) {
            canPromote = true;
        }
    }

    private void addMove(ChessPosition newPosition) {
        if (canPromote) {
            for (ChessPiece.PieceType piece : promotionPieces) {
                moves.add(
                    new ChessMove(myPosition, newPosition, piece)
                );
            }
        } else {
            moves.add(
                new ChessMove(myPosition, newPosition, null)
            );
        }
    }
}