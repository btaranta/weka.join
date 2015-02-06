package weka.join;

import java.util.HashMap;
import java.util.HashSet;

public class Index extends HashMap<Double,HashSet<Integer>> {
	public void map(double value, int position) {
		Double key = new Double(value);
		Integer row = new Integer(position);
		HashSet found = this.get(key);
		if ( found == null ) {
			found = new HashSet<Integer>(row);
			this.put(key, found);
		}
		found.add(row);			
	}	
}
