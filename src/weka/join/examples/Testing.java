package weka.join.examples;

import weka.join.*;

public class Testing {
	
	public static void main(String[] args) {
		
		//Instances table2 = new Instances(ExamplesReader.getInstances("Table2.arff"));
		
		//Instances result = table1.innerJoin(table2, table1.attribute("Id"));
		//Instances result = table1.innerJoin(table2);
		
		Instances table1 = new Instances(ExamplesReader.getInstances("UpdateTarget.arff"));
		Instances table2 = new Instances(ExamplesReader.getInstances("UpdateSource.arff"));
		table1.makeIndex(table1.attribute("Id"));
		table2.makeIndex(table2.attribute("Id"));		
		Instances target = table1.update(table2);	
		
		System.out.println(target);
		
		/*join*/
		/*update*/
		/*union*/
		
	}

}

