package coeditor;

import java.util.ArrayList;

public class ChangeSet {
	int oldLength;
	int newLength;
	ArrayList<Change> changeList;
	
	public ChangeSet() {
		oldLength = newLength = 0;
		changeList = new ArrayList<Change> ();
	}
	
	public ChangeSet(int oldLength, int newLength) {
		this.oldLength = oldLength;
		this.newLength = newLength;
		this.changeList = new ArrayList<Change> ();
	}
	
	public ChangeSet(int oldLength, int newLength, ArrayList<SingleChange> sc) {
		this.oldLength = oldLength;
		this.newLength = newLength;
		this.changeList = zip(sc);
	}
	
	public void addChange(Change c) {
	  this.changeList.add(c);
	}
	
	public SingleChange[] unzip() {
		SingleChange[] unzipped = new SingleChange[this.newLength];
				
		int i = 0;
		for (Change c : this.changeList) {
			switch (c.type) {
				case Change.NEW:
					for (int j = 0; j < c.length; j++) {
						unzipped[i++] = new SingleChange(c.content.charAt(j));
					}
					break;
				case Change.OLD:
					String[] range = c.content.split("-");
					int begin = Integer.parseInt(range[0]);
					for (int j = 0; j < c.length; j++) {
						unzipped[i++] = new SingleChange(begin + j);
					}
					break;
			}
		}

		return unzipped;
	}
	
	public ArrayList<Change> zip(ArrayList<SingleChange> singleChangeList) {
		ArrayList<Change> changeList = new ArrayList<Change> ();
		
		if (singleChangeList.size() == 0)
			return changeList;
		
		Change c = null;
		boolean first = true;
		int type = -1;
		int begin = 0, end = 0;
		String content = "";
		for (SingleChange sc : singleChangeList) {
			if (first) {
				first = false;
				if (sc.type == Change.NEW) {
					content = Character.toString(sc.c);
				} else if (sc.type == Change.OLD) {
					begin = sc.pos;
				} else {}
				type = sc.type;
			} else {
				if (sc.type == type) {
					if (sc.type == Change.NEW)
						content += sc.c;
					else if (sc.type == Change.OLD && sc.pos == end + 1)
						end = sc.pos;
				} else {
					if (sc.type == Change.NEW) {
						changeList.add(new Change(begin, end));
						content = Character.toString(sc.c);
					} else if (sc.type == Change.OLD) {
						changeList.add(new Change(content));
						begin = sc.pos;
					} else {}
					type = sc.type;
				}
			}
		}
		
		if (type == Change.NEW) {
			changeList.add(new Change(content));
		} else if (type == Change.OLD) {
			changeList.add(new Change(begin, end));
		} else {}
		
		return changeList;
	}
	
	public static ChangeSet merge(ChangeSet a, ChangeSet b) {
		if (a.oldLength != b.oldLength) {
			System.err.println("Error in merge.");
			return null;
		}
		
		SingleChange[] unzippedA = a.unzip();
		SingleChange[] unzippedB = b.unzip();
		ArrayList<SingleChange> merged = new ArrayList<SingleChange> ();
		
		int commonLength = Math.min(unzippedA.length, unzippedB.length);
		for (int i = 0; i < commonLength; i++) {
			SingleChange lhs = unzippedA[i];
			SingleChange rhs = unzippedB[i];
			
			if (lhs.type == Change.NEW && rhs.type == Change.NEW) {
				if (lhs.c > rhs.c) {
					merged.add(rhs);
					merged.add(lhs);
				} else {
					merged.add(lhs);
					merged.add(rhs);
				}
			} else if (lhs.type == Change.NEW && rhs.type == Change.OLD) {
				merged.add(lhs);
			} else if (lhs.type == Change.OLD && rhs.type == Change.NEW) {
				merged.add(rhs);
			} else {
				if (lhs.pos == rhs.pos) {
					merged.add(lhs);
				}
			}
		}

		for (int i = commonLength; i < unzippedA.length; i++)
			merged.add(unzippedA[i]);
		
		for (int i = commonLength; i < unzippedA.length; i++)
			merged.add(unzippedA[i]);
		
		int mergedOldLength = a.oldLength;
		int mergedNewLength = merged.size();
		
		System.out.println("m(A, B) unzipped: " + merged);
		
		return new ChangeSet(mergedOldLength, mergedNewLength, merged);
	}
	
	public static ChangeSet follows(ChangeSet a, ChangeSet b) {
		ChangeSet followSet = new ChangeSet();
		
		return followSet;
	}
	
	public String toString() {
		String out = "";
		out = "(" + this.oldLength + " -> " + this.newLength + ") " + this.changeList.toString();
		
		return out;
	}
	
	public static void main(String[] args) {
		ChangeSet a = new ChangeSet(8, 5);
		ChangeSet b = new ChangeSet(8, 5);
		
		a.addChange(new Change(0, 1));
		a.addChange(new Change("si"));
		a.addChange(new Change(7));
		
		b.addChange(new Change(0));
		b.addChange(new Change("e"));
		b.addChange(new Change(6));
		b.addChange(new Change("ow"));
		
		System.out.println("A: " + a);
		System.out.println("B: " + b);
		
		ChangeSet merged = ChangeSet.merge(a, b);
		
		System.out.println("m(A, B): " + merged.toString());
	}
}
