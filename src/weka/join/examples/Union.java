package weka.join.examples;

import java.util.Arrays;

import weka.join.Instances;

public class Union {

	public static void main(String[] args) {

		Instances table1 = new Instances(ExamplesReader.getInstances("Continents.arff"));
		Instances table2 = new Instances(ExamplesReader.getInstances("ContinentsUnion.arff"));
		Instances result;
				
		// Performing union		
		result = table1.union(table2);
		result.setRelationName("union");
		 		
		// Report
		for (Instances table : Arrays.asList(table1, table2, result)) {
			System.out.println(String.format("Contents of %s:\n%s\n", table.relationName(), table));
		}

	}

}
