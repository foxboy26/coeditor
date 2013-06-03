package coeditor;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

public class Document {
	String headText;
	String docId;
	int headRevision;
	ArrayList<RevisionRecord> revisionList;
	Map<String, Integer> activeUsers;
	boolean isOpen;
	
	public Document(String docId) {
		this.revisionList = new ArrayList<RevisionRecord> ();
		this.activeUsers = new Hashtable<String, Integer> ();
		this.docId = docId;
		this.headRevision = -1;
	}
	
	public void open(String clientId) {
		
		if (!isOpen) {
			
			//TODO: connect to S3 to fetch the document.
		  //TODO: test only, change it later
			headText = "abcdefg";
			
			Change initChange = new Change(headText);
			ChangeSet initChangeSet = new ChangeSet(0, headText.length());
			
			initChangeSet.addChange(initChange);
			
			this.addRevisionRecord(clientId, initChangeSet);
			
			isOpen = true;
		} else {
			//TODO
		}		
		
		activeUsers.put(clientId, this.headRevision);
	}
	
	public void save() {
		updateHeadtext();
	}
	
	public void close() {
		save();
		
		isOpen = false;
	}
	
	public ChangeSet applyChangeSet(ChangeSet cs, int revisionNumber) {
		ChangeSet result = cs;
		
		for (int i = revisionNumber; i < headRevision; i++) {
			result = ChangeSet.follows(this.revisionList.get(i).changeSet, cs);
		}
		
		return result;
	}
	
	public void addRevisionRecord(String clientId, ChangeSet cs) {
		headRevision++;
		this.revisionList.add(new RevisionRecord(clientId, headRevision, cs));
	}
	
	public void updateHeadtext() {
		
	}
	
	public void addUser(String clientId, int revisionNumber) {
		activeUsers.put(clientId, revisionNumber);
	}
	
	public void removeUser(String clientId) {
		activeUsers.remove(clientId);
	}
	
	public Set<String> getActiveUser() {
		return this.activeUsers.keySet();
	}
}
