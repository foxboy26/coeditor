package coeditor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Document {
	String headText;
	String docId;
	ArrayList<RevisionRecord> revisionList;
	Set<String> users;
	boolean isOpen;
	
	public Document(String docId) {
		revisionList = new ArrayList<RevisionRecord> ();
		users = new HashSet<String> ();
		this.docId = docId;
	}
	
	public void open(String clientId) {
		
		if (!isOpen) {
			
			//TODO: connect to S3 to fetch the document.
		  //TODO: test only, change it later
			headText = "abcdefg";
			
			Change initChange = new Change(headText);
			ChangeSet initChangeSet = new ChangeSet(0, headText.length());
			initChangeSet.addChange(initChange);
			RevisionRecord initRecord = new RevisionRecord(clientId, 0, initChangeSet);
			this.revisionList.add(initRecord);
			
			isOpen = true;
		} else {
			//TODO
		}		
		
		users.add(clientId);
	}
	
	public void save() {
		
	}
	
	public void close() {
		save();
		
		isOpen = false;
	}
	
	public void addUser(String clientId) {
		users.add(clientId);
	}
	
	public boolean removeUser(String clientId) {
		return users.remove(clientId);
	}
}
