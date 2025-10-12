import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Background thread that periodically refreshes a {@link IRefreshable} component.
 * <p>
 * The thread wakes up at a fixed interval (2 seconds) and calls {@link IRefreshable#refresh()}.
 * Refreshing only occurs if the {@link AtomicBoolean} flag is set to {@code true}.
 * <p>
 * This thread runs as a daemon, allowing the application to exit gracefully.
 */
public class RefreshThread extends Thread {
	private final IRefreshable refreshable;
	private final AtomicBoolean refresh;

	/**
	 * Constructs a new RefreshThread.
	 *
	 * @param refreshable the component to refresh periodically
	 * @param refresh     atomic boolean flag controlling whether a refresh should occur
	 */
	public RefreshThread(IRefreshable refreshable, AtomicBoolean refresh) {
		this.refreshable = refreshable;
		this.refresh = refresh;
		setDaemon(true);
	}

	/**
	 * Main loop of the thread.
	 * <p>
	 * Sleeps for 2 seconds between updates and refreshes the target object
	 * if the {@code refresh} flag is set to {@code true}.
	 * <p>
	 * Exits cleanly when interrupted.
	 */
	@Override
	public void run() {
		while (!isInterrupted()) {
			try {
				Thread.sleep(2000);
				if (refresh.get()) {
					refreshable.refresh();
				}
			} catch (InterruptedException e) {
				// Stop thread on interrupt
				return;
			} catch (Exception e) {
				// Log or ignore other exceptions
			}
		}
	}
}