package jtop;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import jtop.config.Config;
import jtop.core.InfoType;
import jtop.core.RefreshThread;
import jtop.core.ShowProcesses;
import jtop.terminal.InputHandler;
import jtop.terminal.TerminalSize;

/**
 * Core application class that initializes and coordinates all major components
 * of the terminal-based process monitor.
 * <p>
 * This class is responsible for:
 * </p>
 * <ul>
 *	 <li>Configuring the terminal (raw input mode and mouse reporting)</li>
 *	 <li>Initializing configuration and display components</li>
 *	 <li>Starting background refresh and input handling threads</li>
 *	 <li>Ensuring proper cleanup and terminal restoration on exit</li>
 * </ul>
 */
public class App {

	/** Utility for determining terminal dimensions. */
	private final TerminalSize terminalSize = new TerminalSize();

	/** Holds configuration data for runtime settings. */
	private final Config config = new Config();

	/** Handles process retrieval and display logic. */
	private final ShowProcesses showProcesses;

	/** Shared flag for synchronizing display refreshes. */
	private final AtomicBoolean refresh = new AtomicBoolean(true);

	/**
	 * Constructs a new {@code App} instance and initializes the main process display.
	 * <p>
	 * By default, it prepares a {@link ShowProcesses} object displaying
	 * PID, process name, user, CPU usage, and memory usage.
	 * </p>
	 */
	public App() {
		showProcesses = new ShowProcesses(
			InfoType.PID,
			InfoType.NAME,
			InfoType.USER,
			InfoType.CPU,
			InfoType.MEMORY
		);
	}

	/**
	 * Starts the main application loop.
	 * <p>
	 * This method:
	 * </p>
	 * <ul>
	 *	 <li>Enables raw input mode and mouse tracking</li>
	 *	 <li>Draws the initial process table</li>
	 *	 <li>Launches the background refresh thread</li>
	 *	 <li>Delegates user interaction to the {@link InputHandler}</li>
	 * </ul>
	 * When the loop ends, terminal settings are restored and mouse reporting is disabled.
	 *
	 * @throws Exception if an I/O or threading error occurs
	 */
	public void run() throws Exception {
		enableRawMode();
		enableMouseReporting();

		try {
			showProcesses.draw(); // initial draw

			// Start background refresh
			Thread refreshThread = new RefreshThread(showProcesses, refresh);
			refreshThread.setDaemon(true);
			refreshThread.start();

			// Handle user input
			new InputHandler(showProcesses, refresh, terminalSize).start();

		} finally {
			disableMouseReporting();
			restoreTerminal();
		}
	}

	/**
	 * Enables raw input mode on the terminal.
	 * <p>
	 * This disables canonical input processing and echoing,
	 * allowing single keypress handling without pressing Enter.
	 * </p>
	 *
	 * @throws IOException if the process builder fails
	 * @throws InterruptedException if the command execution is interrupted
	 */
	private void enableRawMode() throws IOException, InterruptedException {
		new ProcessBuilder("sh", "-c", "stty raw -echo </dev/tty").inheritIO().start().waitFor();
	}

	/**
	 * Restores terminal settings to normal mode.
	 * <p>
	 * This re-enables standard input behavior and character echoing.
	 * </p>
	 *
	 * @throws IOException if the process builder fails
	 * @throws InterruptedException if the command execution is interrupted
	 */
	private void restoreTerminal() throws IOException, InterruptedException {
		new ProcessBuilder("sh", "-c", "stty sane </dev/tty").inheritIO().start().waitFor();
	}

	/**
	 * Enables mouse reporting mode.
	 * <p>
	 * Allows the application to receive and interpret mouse events
	 * such as clicks and scrolls in the terminal.
	 * </p>
	 */
	private void enableMouseReporting() {
		System.out.print("\u001B[?1000h");
		System.out.flush();
	}

	/**
	 * Disables mouse reporting mode.
	 * <p>
	 * Ensures that the terminal stops sending mouse event sequences
	 * once the program exits or cleans up.
	 * </p>
	 */
	private void disableMouseReporting() {
		System.out.print("\u001B[?1000l");
		System.out.flush();
	}
}