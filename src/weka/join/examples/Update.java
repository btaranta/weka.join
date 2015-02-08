package weka.join.examples;

import weka.join.Instances;
import weka.join.MissingIndexException;

public class Update {

	public static void main(String[] args) throws MissingIndexException  {

		Instances table1 = new Instances(ExamplesReader.getInstances("Countries.arff"));
		Instances table2 = new Instances(ExamplesReader.getInstances("CountriesUpdate.arff"));
		Instances result;
			
		// Indexing fields 
		table1.makeIndex(table1.attribute("Country_Id"));
		table2.makeIndex(table2.attribute("Country_Id"));
				
		// Report  
		System.out.println(table1+"\n");
		System.out.println(table2+"\n");
		result = table1.update(table2);
		result.setRelationName("updated");
		System.out.println(result+"\n");
		
		// NOTE 1: range for nominal variables is updated to match both Instances
		// NOTE 2: SomeNumber Attribute is ignored
	}

}
