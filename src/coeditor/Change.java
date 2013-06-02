package coeditor;

public class Change {
	public static final int NEW = 0;
	public static final int OLD = 1;
	
	int type;
	int length;
	String content;
	
	public Change() {
	}
	
	public Change(char c) {
		this.content = Character.toString(c);
		this.length = content.length();
		this.type = Change.NEW;
	}
	
	public Change(String content) {
		this.content = content;
		this.length = content.length();
		this.type = Change.NEW;
	}
	
	public Change(int pos) {
		this.content = Integer.toString(pos);
		this.length = 1;
		this.type = Change.OLD;
	}
	
	public Change(int begin, int end) {
		this.content = begin + "-" + end;	
		this.length = end - begin + 1;
		this.type = Change.OLD;
	}
	
	public String toString() {
		return this.content;
	}
}
