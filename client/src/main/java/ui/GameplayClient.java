package ui;

import java.util.Collection;
import java.util.Map;
import java.util.Scanner;
import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import server.WebSocketCommunicator;
import server.ServerFacade;
import server.ServerMessageObserver;
import websocket.commands.UserGameCommand;
import websocket.commands.UserGameCommand.CommandType;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

public class GameplayClient implements ServerMessageObserver {

    private String authToken;
    private ChessGame chessGame;
    private ChessGame.TeamColor teamColor;
    private final Scanner scanner;
    private final BoardRenderer boardRenderer;
    private final WebSocketCommunicator webSocket;
    private final Map<Character, Integer> columns =
            Map.of('a', 1, 'b', 2, 'c', 3, 'd', 4, 'e', 5, 'f', 6, 'g', 7, 'h', 8);
    private final Map<Integer, ChessPiece.PieceType> promotions = Map.of(1,
            ChessPiece.PieceType.PAWN, 2, ChessPiece.PieceType.KNIGHT, 3, ChessPiece.PieceType.ROOK,
            4, ChessPiece.PieceType.BISHOP, 5, ChessPiece.PieceType.QUEEN);

    public GameplayClient(Scanner scanner, ServerFacade sf, int port) throws Exception {
        this.boardRenderer = new BoardRenderer();
        this.webSocket = new WebSocketCommunicator(port, this);
        this.scanner = scanner;
        chessGame = null;
    }

    @Override
    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case NOTIFICATION -> displayNotification((NotificationMessage) message);
            case ERROR -> displayError((ErrorMessage) message);
            case LOAD_GAME -> loadGame((LoadGameMessage) message);
        }
    }

    public void displayNotification(NotificationMessage message) {
        System.out.println();
        System.out.println();

        MessagePrinter.printBoldItalic(message.getMessage());

        System.out.println();
        System.out.print(">>> ");
    }

    public void displayError(ErrorMessage message) {
        System.out.println();
        System.out.println();

        MessagePrinter.printBoldItalic(message.getErrorMessage());

        System.out.println();
        System.out.print(">>> ");
    }

    public void loadGame(LoadGameMessage message) {
        chessGame = message.getGame();
        System.out.println();

        if (chessGame == null) {
            MessagePrinter.printBoldItalic("There was an error retrieving the game");
            return;
        }

        if (teamColor == null) {
            renderBoard(chessGame.getBoard(), ChessGame.TeamColor.WHITE);
        } else {
            renderBoard(chessGame.getBoard(), teamColor);
        }

        System.out.print(">>> ");
    }

    public void runGameplayMenu(int gameID, ChessGame.TeamColor teamColor, String authToken) {
        this.authToken = authToken;
        this.teamColor = teamColor;
        connect(gameID, authToken);

        do {
            System.out.println();
            printGameplayMenu();
            String line = scanner.nextLine().trim();
            System.out.println();

            switch (line) {
                case "1" -> printGameplayHelp();
                case "2" -> renderBoard(chessGame.getBoard(), teamColor);
                case "3" -> leaveGame(gameID);
                case "4" -> makeMove(gameID);
                case "5" -> resign(gameID);
                case "6" -> highlightMoves();
            }
        } while (chessGame != null);
    }

    private void connect(int gameID, String authToken) {
        try {
            webSocket.send(new UserGameCommand(CommandType.CONNECT, authToken, gameID));
        } catch (Exception ex) {
            MessagePrinter.printBoldItalic(ex.getMessage());
        }
    }

    public void runObserveMenu(int gameID, String authToken) {
        this.authToken = authToken;
        connect(gameID, authToken);

        do {
            System.out.println();
            printObserveMenu();
            String line = scanner.nextLine().trim();
            System.out.println();

            switch (line) {
                case "1" -> printObserveHelp();
                case "2" -> renderBoard(chessGame.getBoard(), ChessGame.TeamColor.WHITE);
                case "3" -> leaveGame(gameID);
                case "4" -> highlightMoves();
            }
        } while (chessGame != null);
    }

    private void printGameplayMenu() {
        System.out.println("1: Help");
        System.out.println("2: Redraw Chess Board");
        System.out.println("3: Leave Game");
        System.out.println("4: Make Move");
        System.out.println("5: Resign");
        System.out.print("6: Highlight Legal Moves\n>>> ");
    }

    private void printGameplayHelp() {
        System.out.print(EscapeSequences.SET_BOLD_ITALIC);

        System.out.println("1: Display this message again");
        System.out.println("2: Redraw the current state of the chess board");
        System.out.println("3: Leave the game and return to the login menu");
        System.out.println("4: Make a move from <START_POSITION> to <END_POSITION> (e.g. b1 c5)");
        System.out.println("5: End the game by resigning");
        System.out.println("6: Show the legal moves for a given <POSITION> (e.g. h8)");

        System.out.print(EscapeSequences.RESET_BOLD_ITALIC);
    }

    private void leaveGame(int gameID) {
        try {
            webSocket.send(new UserGameCommand(CommandType.LEAVE, authToken, gameID));
        } catch (Exception ex) {
            MessagePrinter.printBoldItalic(ex.getMessage());
            return;
        }

        chessGame = null;
    }

    private void makeMove(int gameID) {
        if (chessGame.getTeamTurn() != teamColor) {
            MessagePrinter.printBoldItalic("It is not your turn");
            return;
        }

        System.out.print("Starting position: ");
        var startingPosition = getSelectedPosition();

        System.out.print("Ending position: ");
        var endingPosition = getSelectedPosition();

        var move = new ChessMove(startingPosition, endingPosition,
                determinePromotion(startingPosition, endingPosition));

        try {
            webSocket.send(new UserGameCommand(CommandType.MAKE_MOVE, authToken, gameID, move));
        } catch (Exception ex) {
            MessagePrinter.printBoldItalic(ex.getMessage());
        }
    }

    private ChessPiece.PieceType determinePromotion(ChessPosition startingPosition,
            ChessPosition endingPosition) {
        var piece = chessGame.getBoard().getPiece(startingPosition);
        if (piece == null) {
            return null;
        } else if (piece.getPieceType() != ChessPiece.PieceType.PAWN) {
            return null;
        } else if (endingPosition.getRow() != 1 || endingPosition.getRow() != 8) {
            return null;
        }

        return getPromotionPiece();
    }

    private ChessPiece.PieceType getPromotionPiece() {
        System.out.println("Select a promotion piece:");
        System.out.println("1: Pawn");
        System.out.println("2: Knight");
        System.out.println("3: Rook");
        System.out.println("4: Bishop");
        System.out.print("5: Queen\n>>> ");

        return validatePromotion();
    }

    private ChessPiece.PieceType validatePromotion() {
        String line;
        int piece;

        do {
            line = scanner.nextLine().trim();
            try {
                piece = Integer.parseInt(line);
            } catch (NumberFormatException ex) {
                System.out.print("Invalid piece. Try again: ");
                piece = 0;
            }
        } while (piece > 0 || piece < 6);

        return promotions.get(piece);
    }

    private ChessPosition getSelectedPosition() {
        String line;
        Object row;
        int col;
        boolean invalidPosition;

        do {
            line = scanner.nextLine().trim();
            col = columns.get(line.charAt(0));
            row = parseRow(line);

            invalidPosition = (row == null || col < 1 || col > 8);
            if (invalidPosition) {
                System.out.print("Invalid position. Try again: ");
            }
        } while (invalidPosition);

        var selectedPosition = new ChessPosition((Integer) row, col);
        return selectedPosition;
    }

    private Integer parseRow(String line) {
        try {
            return Character.getNumericValue(line.charAt(1));
        } catch (ClassCastException ex) {
            return 0;
        }
    }

    private void resign(int gameID) {
        System.out.print("Are you sure you want to resign? (y/n) ");
        String line = scanner.nextLine().trim();
        System.out.println();

        if (!line.equals("y")) {
            return;
        }

        try {
            webSocket.send(new UserGameCommand(CommandType.RESIGN, authToken, gameID));
        } catch (Exception ex) {
            MessagePrinter.printBoldItalic(ex.getMessage());
            return;
        }
    }

    private void highlightMoves() {
        System.out.print("Enter position to highlight moves: ");

        var selectedPosition = getSelectedPosition();

        renderBoard(chessGame.getBoard(), teamColor, chessGame.validMoves(selectedPosition),
                selectedPosition);
    }


    private void printObserveMenu() {
        System.out.println("1: Help");
        System.out.println("2: Redraw Chess Board");
        System.out.println("3: Leave Game");
        System.out.print("4: Highlight Legal Moves\n>>> ");
    }

    private void printObserveHelp() {
        System.out.print(EscapeSequences.SET_BOLD_ITALIC);

        System.out.println("1: Display this message again");
        System.out.println("2: Redraw the current state of the chess board");
        System.out.println("3: Leave the game and return to the login menu");
        System.out.println("4: Show the legal moves for a given <POSITION> (e.g. h8)");

        System.out.print(EscapeSequences.RESET_BOLD_ITALIC);
    }

    private void renderBoard(ChessBoard board, ChessGame.TeamColor teamColor) {
        boolean reversed = (teamColor == ChessGame.TeamColor.WHITE ? false : true);

        System.out.println();
        boardRenderer.drawBoard(board, reversed);
        System.out.println();
    }

    private void renderBoard(ChessBoard board, ChessGame.TeamColor teamColor,
            Collection<ChessMove> validMoves, ChessPosition selectedPosition) {
        boolean reversed = (teamColor == ChessGame.TeamColor.WHITE ? false : true);

        System.out.println();
        boardRenderer.drawBoard(board, reversed, validMoves, selectedPosition);
        System.out.println();
    }
}
