package jtop;
import java.io.IOException;

/**
 * Entry point for the jtop system monitoring application.
 * <p>
 * This class initializes the application and starts the main execution loop.
 * All setup and execution logic is delegated to {@link App}.
 */
public class Main {

    /**
     * Launches the jtop application.
     * <p>
     * Initializes all necessary components, including process monitoring,
     * terminal rendering, and input handling.
     *
     * @param args Command-line arguments (currently ignored)
     * @throws Exception If system information cannot be read or if thread operations fail
     */
    public static void main(String[] args) throws Exception {
        new App().run();
    }
}