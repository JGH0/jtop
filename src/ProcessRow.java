public class ProcessRow {
	public long pid;
	public String name;
	public String path;
	public String user;
	public String cpu;
	public String memory;

	public ProcessRow(long pid, String name, String path, String user, String cpu, String memory) {
		this.pid = pid;
		this.name = name;
		this.path = path;
		this.user = user;
		this.cpu = cpu;
		this.memory = memory;
	}
}