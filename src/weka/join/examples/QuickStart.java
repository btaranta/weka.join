package weka.join.examples;

import java.util.Arrays;
import weka.join.*;

public class QuickStart {
	
	public static void main(String[] args) {
		
		Instances table1 = new Instances(ExamplesReader.getInstances("Countries.arff"));
		Instances table2 = new Instances(ExamplesReader.getInstances("Continents.arff"));
		Instances result;
		
		// Drop attributes not used in this example
		for (String attrName : Arrays.asList("Currency","Population","Updated")) {
			table1.deleteAttributeAt(table1.attribute(attrName).index());
		}
		
		// Performing left join on field Continent_Id
		table1.makeIndex(table1.attribute("Continent_Id"));
		table2.makeIndex(table2.attribute("Continent_Id"));
		result = table1.leftJoin(table2);
		result.setRelationName("result");
		 		
		// Report
		for (Instances table : Arrays.asList(table1, table2, result)) {
			System.out.println(String.format("Contents of %s:\n%s\n", table.relationName(), table));
		}
				
	}

}
 
