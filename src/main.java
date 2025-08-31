//https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/ProcessHandle.html
public class main {
	public static void main(String[] args) {
		// Go through all running processes
		for (ProcessHandle process : ProcessHandle.allProcesses().toList()) {

			// Get information about the process
			ProcessHandle.Info info = process.info();

			// Print PID, the program name, and the user
			String pid = String.valueOf(process.pid());
			String command = info.command().orElse("Unknown");//orElse if before is Null
			String user = info.user().orElse("Unknown");
			String name = command.substring(command.lastIndexOf("/") + 1);
			String tableFormanting = "%-15s %-10s %-10s %-10s %-10s%n";

			System.out.printf(tableFormanting, "Name", "CPU", "Memory", "Disk", "Network");
			System.out.printf(tableFormanting, "Name");
			System.out.println("PID: "+pid+"Name: " + name + ", Command: " + command + ", User: " + user);
		}
	}
}
