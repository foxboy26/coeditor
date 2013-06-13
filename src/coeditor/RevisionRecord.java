package coeditor;

public class RevisionRecord {
	
	String clientId;
	int version;
	ChangeSet changeSet;
	
	public RevisionRecord() {
		clientId = "";
		version = 0;
		changeSet = new ChangeSet();
	}
	
	public RevisionRecord(String clientId, int version, ChangeSet changeSet) {
		this.clientId = clientId;
		this.version = version;
		this.changeSet = changeSet;
	}
	
	public String toString() {
		String out = "";
		out = "clientId: " + clientId + " version: " + version + " changeSet" + changeSet.toString();
	  return out;
	}
}
