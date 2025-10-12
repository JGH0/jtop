package jtop;
import java.io.IOException;

/**
 * Entry point of the program.
 * Handles initialization and starts the application loop.
 *
 * @param args Command-line arguments (currently unused)
 * @throws IOException if reading system files fails
 * @throws InterruptedException if thread operations are interrupted
 */
public class Main {
	public static void main(String[] args) throws Exception {
		new App().run();
	}
}
