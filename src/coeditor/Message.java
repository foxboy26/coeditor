package coeditor;

public class Message {
	static public final int STRING = 0;
	static public final int CHANGESET = 1;

	public String clientId;
	public String action;
	public int contentType;
	public String content;
	
	public Message() {
		this.clientId = "";
		this.action = "";
		this.content = "";
		this.contentType = Message.STRING;
		
	}
	
	public Message(String clientId, String action, int contentType, String content) {
		this.clientId = clientId;
		this.action = action;
		this.contentType = contentType;
		this.content = content;
	}
}
