package coeditor;

public class SingleChange extends Change {

	int pos;
	char c;
	
	public SingleChange(char c) {
	  this.type = Change.NEW;
	  this.c = c;
	  this.length = 1;
  }

	public SingleChange(int pos) {
	  this.type = Change.OLD;
	  this.pos = pos;
	  this.length = 1;
	}
	
	static public SingleChange merge(SingleChange lhs, SingleChange rhs) {
		SingleChange merged = null;
		
		if (lhs.type == Change.NEW && rhs.type == Change.NEW) {
			
		} else if (lhs.type == Change.NEW && rhs.type == Change.OLD) {
			merged = lhs;
		} else if (lhs.type == Change.OLD && rhs.type == Change.NEW) {
			merged = rhs;
		} else {
			if (lhs.pos == rhs.pos) {
				merged = lhs;
			}
		}
		
		System.out.println(merged);
		
		return merged;
	}
	
	public String toString() {
		if (type == Change.NEW)
			return Character.toString(this.c);
		else if (type == Change.OLD)
			return Integer.toString(this.pos);
		else
			return "";
	}
}
