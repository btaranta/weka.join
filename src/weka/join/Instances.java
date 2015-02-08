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
	public Index getIndex() throws MissingIndexException {
		if (!hasIndex()) throw new MissingIndexException();
		return index;
	}
	public void dropIndex() {
		index = null;		
		indexAttr = null;
	}		
	
	private Instances join(Instances source, Attribute key, boolean leftJoin, boolean fullJoin) throws MissingIndexException {
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
	
	public Instances innerJoin(Instances source) throws MissingIndexException {
		return this.join(source,null,false,false);
	}
	public Instances fullJoin(Instances source) throws MissingIndexException {
		return this.join(source,null,true,true);
	}
	public Instances leftJoin(Instances source) throws MissingIndexException {
		return this.join(source,null,true,false);
	}	
	public Instances innerJoin(Instances source, Attribute key) throws MissingIndexException {
		return this.join(source,key,false,false);
	}
	public Instances fullJoin(Instances source, Attribute key) throws MissingIndexException {
		return this.join(source,key,true,true);
	}
	public Instances leftJoin(Instances source, Attribute key) throws MissingIndexException {
		return this.join(source,key,true,false);
	}
		
	public Instances update(Instances source, Attribute key) throws MissingIndexException {
		// Prepare target Instances and select Attributes to copy from source
		Instances target = new Instances(this,0,0);		
		// Mapping of Attribute: source -> target
		HashMap<Attribute,Attribute> paired = new HashMap();
		Enumeration<Attribute> sourceAttrs = source.enumerateAttributes();		
		while (sourceAttrs.hasMoreElements()) {
			Attribute sourceAttr = sourceAttrs.nextElement();
			Enumeration<Attribute> attrs = target.enumerateAttributes();			
			while (attrs.hasMoreElements()) {
				Attribute attr = attrs.nextElement();							
				if ( sourceAttr.name().equals(attr.name()) && sourceAttr.type() == attr.type() && attr != key ) {
					// Rebuilding range of nominal levels
					if (attr.isNominal()) {
						HashSet<String> levels = new HashSet<String>();
						for (Enumeration<String> values : Arrays.asList(attr.enumerateValues(), sourceAttr.enumerateValues())) 
							while (values.hasMoreElements()) 
								levels.add(values.nextElement());
						FastVector levelsAttribute = new FastVector();
						for (String level : levels)
							levelsAttribute.addElement(level);
						int position = attr.index();
						target.deleteAttributeAt(position);
						attr = new Attribute(attr.name(), levelsAttribute);
						target.insertAttributeAt(attr, position);
						paired.put(sourceAttr, attr);
					} else paired.put(sourceAttr, attr);
				}
			}			
		}
		// Get sourceIndex 
		Index sourceIndex = source.getIndex();  		
		// Iterate through all Instance (full scan)
		Enumeration<Instance> rows = this.enumerateInstances();
		while (rows.hasMoreElements()) {
			Instance row = rows.nextElement();
			Double sourceKey = new Double(row.value(key));
			if (sourceIndex.containsKey(sourceKey)) {
				for (Integer position : sourceIndex.get(sourceKey)) {					
					Instance sourceRow = source.instance(position);
					double[] rawData = row.toDoubleArray();
					for (Entry<Attribute,Attribute> pair : paired.entrySet()) {
						Attribute sourceAttr = pair.getKey();
						Attribute targetAttr = pair.getValue();
						int index = (int) target.attribute(targetAttr.name()).index();						
						if (sourceRow.isMissing(sourceAttr)) {
							rawData[index] = Instance.missingValue();
						} else {
							if (targetAttr.isNumeric() || targetAttr.isDate() ) rawData[index] = sourceRow.value(sourceAttr);
							if (targetAttr.isNominal()) {								
								String level = sourceAttr.value((int)sourceRow.value(sourceAttr));							
								rawData[index] = (double) targetAttr.indexOfValue(level); 
							}				
							if (targetAttr.isString()) {
								String level = sourceAttr.value((int)sourceRow.value(sourceAttr)); 
								rawData[index] = (double) target.attribute(targetAttr.name()).addStringValue(level);
							}
						}
					}
					target.add(new Instance(1.0, rawData));
				}				
			} else target.add(new Instance(row));			
		}
		return target;
	}	
	public Instances update(Instances source) throws MissingIndexException {
		return update(source, indexAttr);
	}
	
	public Instances union(Instances source) {
		// Prepare target Instances and select Attributes to copy from source
		Instances target = new Instances(this,0,0);		
		// Merging attributes from source and this into target 
		Enumeration<Attribute> sourceAttrs = source.enumerateAttributes();		
		while (sourceAttrs.hasMoreElements()) {
			Attribute sourceAttr = sourceAttrs.nextElement();
			Enumeration<Attribute> attrs = target.enumerateAttributes();
			boolean found = false;
			while (attrs.hasMoreElements()) {
				Attribute attr = attrs.nextElement();
				if (sourceAttr.name().equals(attr.name())) {
					found = true;
					if ( sourceAttr.type() == attr.type() ) {
						if ( attr.isNominal() ) {
							HashSet<String> levels = new HashSet<String>();
							for (Enumeration<String> values : Arrays.asList(attr.enumerateValues(), sourceAttr.enumerateValues())) 
								while (values.hasMoreElements()) 
									levels.add(values.nextElement());
							FastVector levelsAttribute = new FastVector();
							for (String level : levels)
								levelsAttribute.addElement(level);
							int position = attr.index();
							target.deleteAttributeAt(position);
							attr = new Attribute(attr.name(), levelsAttribute);
							target.insertAttributeAt(attr, position);
						}
					}
				}
			}
			if (!found) {
				target.insertAttributeAt((Attribute) sourceAttr.copy(), target.numAttributes());
			}
		}				
		// Copy all Instance from this (full scan), set missing from Attributes from source 
		Enumeration<Instance> rows = this.enumerateInstances();
		while (rows.hasMoreElements()) {
			double[] rawData = Arrays.copyOf(rows.nextElement().toDoubleArray(), target.numAttributes());
			for (int k=this.numAttributes(); k < rawData.length; k++)
				rawData[k] = Instance.missingValue();
			target.add(new Instance(1.0, rawData));
		}
		// Copy all Instance from source (full scan), set missing from Attributes from source or merge them		
		rows = source.enumerateInstances();
		while (rows.hasMoreElements()) {
			Instance sourceRow = rows.nextElement();
			double[] rawData = new double[target.numAttributes()];
			for (int k=0; k < rawData.length; k++) 
				rawData[k] = Instance.missingValue();
			sourceAttrs = source.enumerateAttributes();
			while (sourceAttrs.hasMoreElements()) {
				Attribute sourceAttr = sourceAttrs.nextElement();
				Attribute targetAttr = target.attribute(sourceAttr.name());
				if (targetAttr != null) {
					int index = (int) target.attribute(targetAttr.name()).index();						
					if (sourceRow.isMissing(sourceAttr)) {
						rawData[index] = Instance.missingValue();
					} else {
						if (targetAttr.isNumeric() || targetAttr.isDate() ) rawData[index] = sourceRow.value(sourceAttr);
						if (targetAttr.isNominal()) {								
							String level = sourceAttr.value((int)sourceRow.value(sourceAttr));							
							rawData[index] = (double) targetAttr.indexOfValue(level); 
						}				
						if (targetAttr.isString()) {
							String level = sourceAttr.value((int)sourceRow.value(sourceAttr)); 
							rawData[index] = (double) target.attribute(targetAttr.name()).addStringValue(level);
						}
					}
				}
			}			
			target.add(new Instance(1.0, rawData));
		}
	return target;
	}
	

}
