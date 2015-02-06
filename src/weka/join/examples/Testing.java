package weka.join.examples;

import weka.join.*;

public class Testing {
	
	public static void main(String[] args) {
		
		//Instances table2 = new Instances(ExamplesReader.getInstances("Table2.arff"));
		
		//Instances result = table1.innerJoin(table2, table1.attribute("Id"));
		//Instances result = table1.innerJoin(table2);
		
		Instances table1 = new Instances(ExamplesReader.getInstances("Countries.arff"));
		
		System.out.println(table1);
		
		/*join*/
		/*update*/
		/*union*/
		
	}

}

