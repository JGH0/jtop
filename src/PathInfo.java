import java.util.Optional;

public class PathInfo{
	public static String getName(long pid){
		Optional<ProcessHandle> ph = ProcessHandle.of(pid);
		if (ph.isPresent()){
			ProcessHandle.Info info = ph.get().info();
			String command = info.command().orElse("Unknown");
			return command.substring(command.lastIndexOf("/") + 1);
		}
		return "Unknown";
	}

	public static String getPath(long pid){
		Optional<ProcessHandle> ph = ProcessHandle.of(pid);
		if (ph.isPresent()){
			ProcessHandle.Info info = ph.get().info();
			String command = info.command().orElse("Unknown");
			return command;
		}
		return "Unknown";
	}
}
