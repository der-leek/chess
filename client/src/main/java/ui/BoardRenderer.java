package ui;

import chess.*;
import java.util.Collection;
import java.util.HashSet;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class BoardRenderer {
    public static void main(String[] args) {
        var br = new BoardRenderer();
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        br.drawBoard(board, false);
        br.out.println();
        br.drawBoard(board, true);;
    }

    private final PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
    private final String[] columns = {"a", "b", "c", "d", "e", "f", "g", "h"};
    private final int boardSize = 8;

    private boolean reversed;
    private ChessBoard board;
    private ChessPosition selectedPosition;

    private Collection<ChessPosition> validPositions;

    public BoardRenderer() {
        reversed = false;
        board = new ChessBoard();
        selectedPosition = null;
        validPositions = null;
    }

    public void drawBoard(ChessBoard board, boolean reversed) {
        this.board = board;
        this.reversed = reversed;
        drawBoard();
    }

    public void drawBoard(ChessBoard board, boolean reversed, Collection<ChessMove> validMoves,
            ChessPosition selectedPosition) {
        this.board = board;
        this.reversed = reversed;
        this.selectedPosition = selectedPosition;
        extractMoves(validMoves);
        drawBoard();
    }

    public void drawBoard() {
        out.print(EscapeSequences.SET_TEXT_BOLD);
        drawHeader();

        int i = (reversed ? 1 : boardSize);
        int step = (reversed ? 1 : -1);
        boolean condition = true;

        while (condition) {
            drawRow(i, EscapeSequences.SET_BG_COLOR_WHITE, EscapeSequences.SET_BG_COLOR_BLACK);
            i += step;
            drawRow(i, EscapeSequences.SET_BG_COLOR_BLACK, EscapeSequences.SET_BG_COLOR_WHITE);
            i += step;
            condition = (reversed ? i <= boardSize : i > 0);
        }

        drawHeader();
        out.print(EscapeSequences.RESET_TEXT_BOLD_FAINT);
        selectedPosition = null;
        validPositions = null;
    }

    private void extractMoves(Collection<ChessMove> moves) {
        if (validPositions == null) {
            validPositions = new HashSet<>();
        } else {
            validPositions.clear();
        }

        for (ChessMove move : moves) {
            validPositions.add(move.getEndPosition());
        }
    }

    private void drawHeader() {
        out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY);
        out.print(EscapeSequences.EMPTY);

        int i = (reversed ? boardSize - 1 : 0);
        int step = (reversed ? -1 : 1);
        boolean condition = true;

        while (condition) {
            out.printf(" %s ", columns[i]);
            i += step;
            condition = (reversed ? i >= 0 : i < boardSize);
        }

        out.print(EscapeSequences.EMPTY);
        out.print(EscapeSequences.RESET_BG_COLOR);
        out.print("\n");
    }

    private void drawRow(int index, String startColor, String endColor) {
        out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY);
        out.printf(" %s ", index);

        int i = (reversed ? boardSize : 1);
        int step = (reversed ? -1 : 1);
        boolean condition = true;

        while (condition) {
            drawSquare(i, index, startColor);
            i += step;
            drawSquare(i, index, endColor);
            i += step;
            condition = (reversed ? i > 0 : i <= boardSize);
        }

        out.print(EscapeSequences.RESET_TEXT_COLOR);
        out.print(EscapeSequences.RESET_TEXT_ITALIC);
        out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY);
        out.printf(" %s ", index);
        out.print(EscapeSequences.RESET_BG_COLOR);
        out.print("\n");
    }

    private void drawSquare(int x, int y, String backgroundColor) {
        var renderedPosition = new ChessPosition(y, x);

        if (selectedPosition != null && validPositions != null) {
            backgroundColor = setBackgroundColor(backgroundColor, renderedPosition);
        }

        ChessPiece piece = board.getPiece(renderedPosition);


        StringBuilder output = new StringBuilder();
        output.append(backgroundColor);

        if (piece == null) {
            output.append(EscapeSequences.EMPTY);
        } else {
            setPieceColor(piece, output);
        }

        out.print(output.toString());
    }

    private String setBackgroundColor(String backgroundColor, ChessPosition renderedPosition) {
        if (selectedPosition.equals(renderedPosition)) {
            backgroundColor = EscapeSequences.SET_BG_COLOR_YELLOW;
        } else if (validPositions.contains(renderedPosition)) {
            if (backgroundColor.equals(EscapeSequences.SET_BG_COLOR_WHITE)) {
                backgroundColor = EscapeSequences.SET_BG_COLOR_GREEN;
            } else {
                backgroundColor = EscapeSequences.SET_BG_COLOR_DARK_GREEN;
            }
        }
        return backgroundColor;
    }

    private void setPieceColor(ChessPiece piece, StringBuilder output) {
        String pieceColor = (piece.getTeamColor() == ChessGame.TeamColor.WHITE)
                ? EscapeSequences.SET_TEXT_COLOR_CORAL
                : EscapeSequences.SET_TEXT_COLOR_BLUE;

        output.append(pieceColor);
        output.append(EscapeSequences.SET_TEXT_ITALIC);
        output.append(String.format(" %s ", piece.toString()));
    }
}
