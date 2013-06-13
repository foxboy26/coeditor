package coeditor;

import java.util.ArrayList;

import com.google.gson.Gson;

public class ChangeSet {
	int oldLength;
	int newLength;
	ArrayList<Change> changeList;
	
	public ChangeSet() {
		oldLength = newLength = 0;
		changeList = new ArrayList<Change> ();
	}
	
	public ChangeSet(String content) {
		oldLength = 0;
		newLength = content.length();
		changeList = new ArrayList<Change> ();
		changeList.add(new Change(content));
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
					begin = end = sc.pos;
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
						begin = end = sc.pos;
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
		
		for (int i = commonLength; i < unzippedB.length; i++)
			merged.add(unzippedA[i]);
		
		int mergedOldLength = a.oldLength;
		int mergedNewLength = merged.size();
		
		System.out.println("m(A, B) unzipped: " + merged);
		
		return new ChangeSet(mergedOldLength, mergedNewLength, merged);
	}
	
	public static ChangeSet follows(ChangeSet a, ChangeSet b) {
		SingleChange[] unzippedA = a.unzip();
		SingleChange[] unzippedB = b.unzip();
		ArrayList<SingleChange> followSet = new ArrayList<SingleChange> ();
		
		int commonLength = Math.min(unzippedA.length, unzippedB.length);
		for (int i = 0; i < commonLength; i++) {
			SingleChange lhs = unzippedA[i];
			SingleChange rhs = unzippedB[i];
			
			if (lhs.type == Change.NEW && rhs.type == Change.NEW) {
				if (lhs.c > rhs.c) {
					followSet.add(rhs);
					followSet.add(new SingleChange(i));
				} else {
					followSet.add(new SingleChange(i));
					followSet.add(rhs);
				}
			} else if (lhs.type == Change.NEW && rhs.type == Change.OLD) {
				followSet.add(new SingleChange(i));
			} else if (lhs.type == Change.OLD && rhs.type == Change.NEW) {
				followSet.add(rhs);
			} else {
				if (lhs.pos == rhs.pos) {
					followSet.add(lhs);
				}
			}
		}

		for (int i = commonLength; i < unzippedA.length; i++)
			if (unzippedA[i].type == Change.NEW)
				followSet.add(new SingleChange(i));
		
		for (int i = commonLength; i < unzippedB.length; i++)
			if (unzippedB[i].type == Change.NEW)
				followSet.add(unzippedB[i]);
		
		int followsOldLength = a.newLength;
		int followsNewLength = followSet.size();
		return new ChangeSet(followsOldLength, followsNewLength, followSet);
	}
	
	public static ChangeSet composition(ChangeSet a, ChangeSet b) {
		SingleChange[] unzippedA = a.unzip();
		SingleChange[] unzippedB = b.unzip();
		ArrayList<SingleChange> compositionSet = new ArrayList<SingleChange> ();
		
		for (SingleChange sc : unzippedB) {
			if (sc.type == Change.NEW)
				compositionSet.add(sc);
			else if (sc.type == Change.OLD)
				compositionSet.add(unzippedA[sc.pos]);
		}
		
		return new ChangeSet(a.oldLength, b.newLength, compositionSet);
	}
	
	public String applyTo(String document) {
		
		String newDoc = "";
		
		for (Change c : this.changeList) {
			if (c.type == Change.NEW) {
				newDoc = newDoc + c.content;
			} else {
				String[] range = c.content.split("-");
				int begin = Integer.parseInt(range[0]);
				System.out.println(begin+c.length);
				newDoc = newDoc + document.substring(begin, begin + c.length);
			}
		}
		
		return newDoc;
	}
	
	public String toString() {
		String out = "";
		out = "(" + this.oldLength + " -> " + this.newLength + ") " + 
				this.changeList.toString();
		
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
		
		ChangeSet fab = ChangeSet.follows(a, b);
		System.out.println("f(A, B): " + fab.toString());
		ChangeSet fba = ChangeSet.follows(b, a);
		System.out.println("f(B, A): " + fba.toString());
		
		ChangeSet ab = ChangeSet.composition(a, fab);
		System.out.println("Af(A, B): " + ab.toString());

		ChangeSet ba = ChangeSet.composition(b, fba);
		System.out.println("Bf(B, A): " + ba.toString());
		
		Gson gson = new Gson();
		String json = gson.toJson(ba);
		System.out.println(json);
		
		ChangeSet test = gson.fromJson(json, ChangeSet.class);
		
		System.out.println(test);
		
		ChangeSet headChange = new ChangeSet("hah");
		System.out.println(headChange);
		
		
		String headText = "baseball";
		
		ChangeSet test1 = new ChangeSet(0, 14);
		ChangeSet test2 = new ChangeSet(14, 15);
		
		a.addChange(new Change("ldldaaaaaaao x"));
		
		b.addChange(new Change(0, 13));
		b.addChange((new Change("d")));
		
		System.out.println(ChangeSet.follows(headChange, ba));
		System.out.println(b.applyTo(headText));
	}
}