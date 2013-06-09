package coeditor;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import storage.KeyValueStore;

public class Document {
	String headText;
	String docId;
	int headRevision;
	ArrayList<RevisionRecord> revisionList;
	Map<Integer, Client> activeUsers;
	boolean isOpen;
	KeyValueStore storage;
	
	public Document(String documentId, KeyValueStore storage) {
		this.storage = storage;
		this.docId = documentId;
		this.revisionList = new ArrayList<RevisionRecord> ();
		this.activeUsers = new Hashtable<Integer, Client> ();
		this.headRevision = -1;
	}
	
	public void open(int connectionId, String clientId) {
		
		Client newClient = new Client(clientId);
		
		if (!isOpen) {
			//TODO: connect to S3 to fetch the document.
			headText = storage.getDocument(docId);
			
			Change initChange = new Change(headText);
			ChangeSet initChangeSet = new ChangeSet(0, headText.length());
			
			initChangeSet.addChange(initChange);
			
			this.addRevisionRecord(clientId, initChangeSet);

			newClient.latestVersion = headRevision;
			
			isOpen = true;
		} else {
			//TODO
		}		
		
		this.addUser(connectionId, newClient);
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
	
	public void addUser(Integer clientId, Client user) {
		activeUsers.put(clientId, user);
	}
	
	public void removeUser(Integer clientId) {
		activeUsers.remove(clientId);
	}
	
	public Set<Integer> getActiveUser() {
		return this.activeUsers.keySet();
	}
}
