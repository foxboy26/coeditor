package coeditor;

public class Client {
	public String clientId;
	public int latestVersion;
	
	public Client() {
		clientId = "";
		latestVersion = 0;
	}
	
	public Client(String clientId) {
		this.clientId = clientId;
		this.latestVersion = 0;
	}
}
