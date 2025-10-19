package jtop.terminal;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import jtop.core.ShowProcesses;

/**
 * Handles keyboard and mouse input from the user for the process monitor.
 * <p>
 * Interprets key presses for:
 * </p>
 * <ul>
 *	 <li>Scrolling (Arrow keys, 'j'/'k', mouse wheel)</li>
 *	 <li>Sorting by column (mouse click on header)</li>
 *	 <li>Paging (Enter key)</li>
 *	 <li>Exiting the application ('q' or Ctrl+C)</li>
 * </ul>
 */
public class InputHandler {

	/** The main process display manager. */
	private final ShowProcesses showProcesses;

	/** Atomic flag indicating whether the process table should be refreshed. */
	private final AtomicBoolean refresh;

	/** Provides the current terminal size. */
	private final TerminalSize terminalSize;

	/**
	 * Creates a new input handler for a given process table and terminal.
	 *
	 * @param showProcesses the {@link ShowProcesses} instance to control
	 * @param refresh atomic boolean controlling background refresh
	 * @param terminalSize the {@link TerminalSize} instance
	 */
	public InputHandler(ShowProcesses showProcesses, AtomicBoolean refresh, TerminalSize terminalSize) {
		this.showProcesses = showProcesses;
		this.refresh = refresh;
		this.terminalSize = terminalSize;
	}

	/**
	 * Starts reading and handling user input.
	 * <p>
	 * This method blocks and continuously interprets keyboard and mouse events
	 * until the user exits the application.
	 * </p>
	 *
	 * @throws Exception if an I/O error occurs while reading input
	 */
	public void start() throws Exception {
		int pageSize = terminalSize.getRows() - 3;
		int c;

		while ((c = System.in.read()) != -1) {
			switch (c) {
				case 27: // ESC sequence
					if (System.in.read() == 91) { // '['
						int next = System.in.read();
						switch (next) {
							case 65 -> showProcesses.scrollUp();	 // Arrow Up
							case 66 -> showProcesses.scrollDown();   // Arrow Down
							case 77 -> handleMouseEvent();		   // Mouse event
						}
						showProcesses.draw();
						refresh.set(true);
					}
					break;

				case 106: // 'j' key
					showProcesses.scrollDown();
					showProcesses.draw();
					refresh.set(true);
					break;

				case 107: // 'k' key
					showProcesses.scrollUp();
					showProcesses.draw();
					refresh.set(true);
					break;

				case 13: // Enter key
					for (int i = 0; i < pageSize; i++) {
						showProcesses.scrollDown();
					}
					showProcesses.draw();
					refresh.set(true);
					break;

				default:
					if (c >= 48 && c <= 57) { // 0-9
						if (c == 48) {
							c = 58;// 0 acts as 10 and 1 is the first index
						}
						showProcesses.changeSort(c - 49);
					}
					if (c == 113 || c == 3) { // 'q' or Ctrl+C
						return; // exit loop
					}
			}
		}
	}

	/**
	 * Handles a mouse event received from the terminal.
	 * <p>
	 * Interprets:
	 * </p>
	 * <ul>
	 *	 <li>Left click on header row → changes sorting column</li>
	 *	 <li>Scroll wheel up → scrolls up</li>
	 *	 <li>Scroll wheel down → scrolls down</li>
	 * </ul>
	 *
	 * @throws Exception if an I/O error occurs while reading mouse input
	 */
	private void handleMouseEvent() throws Exception {
		int cb = System.in.read() - 32; // button code
		int cx = System.in.read() - 32; // column (X)
		int cy = System.in.read() - 32; // row (Y)

		switch (cb) {
			case 0 -> { // Left click
				if (cy == 1) {
					showProcesses.changeSortByClick(cx - 1);
				}
			}
			case 64 -> showProcesses.scrollUp();   // wheel up
			case 65 -> showProcesses.scrollDown(); // wheel down
		}
	}
}