//https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/ProcessHandle.html
public class main {
	public static void main(String[] args) {
		ProcessHandle.allProcesses().forEach(ph -> {
        	ProcessHandle.Info info = ph.info();
        	System.out.printf("PID: %d, Command: %s, User: %s%n",
        		ph.pid(),
        		info.command().orElse("Unknown"),
    			info.user().orElse("Unknown")
			);
		});
	}
}