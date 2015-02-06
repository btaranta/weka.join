package weka.join;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;


public class Instances extends weka.core.Instances {
	private static final long serialVersionUID = 1L;
	public Instances(weka.core.Instances dataset) {
		super(dataset);
	}
	public Instances(Instances source, int first, int toCopy) {
		super(source, first,toCopy);
	}
	
	private Index index;
	private Attribute indexAttr;
	public void makeIndex(Attribute attr) {		
		index = new Index();
		indexAttr = attr; 
		double[] values = attributeToDoubleArray(attr.index());		
		for (int position=0; position<values.length; position++) 
			if (!Double.isNaN(values[position]))
				index.map(values[position], position);
	}
	public boolean hasIndex() {
		return (index != null);
	}
	public Index getIndex() {
		return index;
	}
	public void dropIndex() {
		index = null;
		indexAttr = null;
	}		
	
	private Instances join(Instances source, Attribute key, boolean leftJoin, boolean fullJoin) {
		
		// Prepare target Instances and select Attributes to copy from source
		Instances target = new Instances(this,0,0);		
		Enumeration<Attribute> attributes = source.enumerateAttributes();
		List<Attribute> duplicated = new ArrayList<Attribute>();
		while (attributes.hasMoreElements()) {
			Attribute attr = (Attribute) attributes.nextElement();
			if (target.attribute(attr.name()) == null ) {
				target.insertAttributeAt((Attribute)attr.copy(), target.numAttributes());				
			} else duplicated.add(attr);
		}				
		// Get sourceIndex and make a copy for faster fullJoin
		Index sourceIndex = source.getIndex();
		Index sourceIndexUnused = null;
		if (fullJoin) sourceIndexUnused = (Index) sourceIndex.clone();  		
		// Iterate through all Instance using Index or dummyIndex (full scan)		
		Iterator<Entry<Double, HashSet<Integer>>> rowIterator;
		if ( key == null )	
			rowIterator = index.entrySet().iterator();
			else rowIterator = new DummyIndex(this, key);		
		while (rowIterator.hasNext()) {
			Entry<Double, HashSet<Integer>> rowGroup = rowIterator.next();
			// Inner Join
			if (sourceIndex.containsKey(rowGroup.getKey())) {				 
				for (Integer pos : rowGroup.getValue()) for (Integer sourcePos : sourceIndex.get(rowGroup.getKey())) {									
					Instance row = (Instance) this.instance(pos);
					Instance sourceRow = new Instance(source.instance(sourcePos)); 					
					for (Attribute duplicate : duplicated) sourceRow.deleteAttributeAt(duplicate.index()); 
					target.add(row.mergeInstance(sourceRow));
				}
				if (fullJoin) sourceIndexUnused.remove(rowGroup.getKey());
			// Left Join
			} else if (leftJoin) {				
				Instance sourceRow = new Instance(source.firstInstance());
				for (Attribute duplicate : duplicated) sourceRow.deleteAttributeAt(duplicate.index());
				for (int i=0; i<sourceRow.numAttributes(); i++) sourceRow.setMissing(i);
				for (Integer pos : rowGroup.getValue()) {
					Instance row = (Instance) this.instance(pos);
					target.add(row.mergeInstance(sourceRow));
				}
			}
		} 		
		// Full Join
		if (fullJoin) {
			Instance row = new Instance(this.firstInstance());
			for (int i=0; i<row.numAttributes(); i++) row.setMissing(i);
			for (Entry<Double, HashSet<Integer>> sourceRowGroup : sourceIndexUnused.entrySet()) {
				for (Integer sourcePos : sourceRowGroup.getValue()) {
					Instance sourceRow = new Instance(source.instance(sourcePos)); 					
					for (Attribute duplicate : duplicated) sourceRow.deleteAttributeAt(duplicate.index());				
					target.add(row.mergeInstance(sourceRow));
				}
			}
		}
		return target;
	}		
	

	public Instances innerJoin(Instances source) {
		return this.join(source,null,false,false);
	}
	public Instances fullJoin(Instances source) {
		return this.join(source,null,true,true);
	}
	public Instances leftJoin(Instances source) {
		return this.join(source,null,true,false);
	}	
	public Instances innerJoin(Instances source, Attribute key) {
		return this.join(source,key,false,false);
	}
	public Instances fullJoin(Instances source, Attribute key) {
		return this.join(source,key,true,true);
	}
	public Instances leftJoin(Instances source, Attribute key) {
		return this.join(source,key,true,false);
	}
		
	public Instances update(Instances source, Attribute key) {
		// to merge with other file
		return null;
	}
	public Instances update(Instances source) {
		return update(source, source.indexAttr); 
	}

}
