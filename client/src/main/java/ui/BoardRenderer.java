package ui;

import chess.*;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class BoardRenderer {
    private final PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
    private final String[] columns = {"a", "b", "c", "d", "e", "f", "g", "h"};
    private final int boardSize = 8;
    private ChessBoard board;

    public BoardRenderer(ChessBoard board) {
        this.board = board;
        this.board.resetBoard();
    }

    public static void main(String[] args) {
        var br = new BoardRenderer(new ChessBoard());
        br.drawBoard(false);
        br.out.println();
        br.drawBoard(true);;
    }

    public void drawBoard(boolean reversed) {
        out.print(EscapeSequences.SET_TEXT_BOLD);
        drawHeader(reversed);

        int i = (reversed ? 1 : boardSize);
        int step = (reversed ? 1 : -1);
        boolean condition = true;

        while (condition) {
            drawRow(i, EscapeSequences.SET_BG_COLOR_WHITE, EscapeSequences.SET_BG_COLOR_BLACK,
                    reversed);
            i += step;
            drawRow(i, EscapeSequences.SET_BG_COLOR_BLACK, EscapeSequences.SET_BG_COLOR_WHITE,
                    reversed);
            i += step;
            condition = (reversed ? i <= boardSize : i > 0);
        }

        drawHeader(reversed);
        out.print(EscapeSequences.RESET_TEXT_BOLD_FAINT);
    }

    private void drawSquare(int x, int y, String backgroundColor) {
        ChessPiece piece = board.getPiece(new ChessPosition(y, x));
        StringBuilder output = new StringBuilder();
        output.append(backgroundColor);

        if (piece == null) {
            output.append(EscapeSequences.EMPTY);
        } else {
            setPieceColor(piece, output);
        }

        out.print(output.toString());
    }

    private void setPieceColor(ChessPiece piece, StringBuilder output) {
        String pieceColor = (piece.getTeamColor() == ChessGame.TeamColor.WHITE)
                ? EscapeSequences.SET_TEXT_COLOR_CORAL
                : EscapeSequences.SET_TEXT_COLOR_BLUE;

        output.append(pieceColor);
        output.append(EscapeSequences.SET_TEXT_ITALIC);
        output.append(String.format(" %s ", piece.toString()));
    }

    private void drawRow(int index, String startColor, String endColor, boolean reversed) {
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

    private void drawHeader(boolean reversed) {
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
}
