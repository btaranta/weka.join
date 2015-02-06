package weka.join;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

import weka.core.Attribute;

public class DummyIndex implements Iterator<Entry<Double, HashSet<Integer>>> {
	private double[] keys;
	private Integer positionLeft;
	private Integer position = 0;		 
	public DummyIndex(Instances instances, Attribute attr) {
		keys = instances.attributeToDoubleArray(attr.index());
		positionLeft = keys.length;			
	}
	@Override
	public boolean hasNext() {
		return ( positionLeft > 0 );
	}
	@Override
	public Entry<Double, HashSet<Integer>> next() {
		final Double key = new Double(keys[position]);
		final HashSet<Integer> value = new HashSet<Integer>(Arrays.asList(position));
		position++;
		positionLeft--;			
		return new Entry<Double, HashSet<Integer>>() {
			private Double k = key;
			private HashSet<Integer> v = value;							
			@Override
			public Double getKey() {
				return key;
			}
			@Override
			public HashSet<Integer> getValue() {					
				return value;
			}
			@Override
			public HashSet<Integer> setValue(HashSet<Integer> value) {
				return null;
			}
		};
	}
	@Override
	public void remove() {}
}
