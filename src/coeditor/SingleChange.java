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
	
	public String toString() {
		if (type == Change.NEW)
			return Character.toString(this.c);
		else if (type == Change.OLD)
			return Integer.toString(this.pos);
		else
			return "";
	}
}
