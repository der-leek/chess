package ui;

public class MessagePrinter {
    public static void printBoldItalic(String message) {
        System.out.print(EscapeSequences.SET_BOLD_ITALIC);
        System.out.println(message);
        System.out.print(EscapeSequences.RESET_BOLD_ITALIC);
    }
}
