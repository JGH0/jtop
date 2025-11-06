import java.io.IOException;

public class MouseTest {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Mouse test. Click inside the terminal. Press 'q' to quit.");

        // Enable raw input (no buffering) and mouse reporting
        new ProcessBuilder("sh", "-c", "stty raw -echo </dev/tty").inheritIO().start().waitFor();
        System.out.print("\u001B[?1002h"); // enable mouse tracking (button-event mode)
        System.out.flush();

        try {
            while (true) {
                int c = System.in.read();
                if (c == 113) break; // 'q' to quit
                if (c == 27) {        // ESC sequence
                    int next = System.in.read();
                    if (next == 91) { // '['
                        int code = System.in.read();
                        if (code == 77) { // 'M' → mouse event
                            int cb = System.in.read() - 32;
                            int cx = System.in.read() - 32; // X (column)
                            int cy = System.in.read() - 32; // Y (row)
                            System.out.printf("\r"+"Mouse event: cb=%d, X=%d, Y=%d%n", cb, cx, cy);
                        }
                    }
                }
            }
        } finally {
            // restore terminal
            new ProcessBuilder("sh", "-c", "stty sane </dev/tty").inheritIO().start().waitFor();
            System.out.print("\u001B[?1002l"); // disable mouse tracking
            System.out.flush();
        }
    }
}
