package weka.join.examples;

import java.lang.reflect.Method;
import java.util.Arrays;


import weka.join.*;

public class JoinsCircus {
	
	public static void main(String[] args) throws Exception {
		
		Instances table1 = new Instances(ExamplesReader.getInstances("Countries.arff"));
		Instances table2 = new Instances(ExamplesReader.getInstances("Continents.arff"));
		Instances result;
			
		// Indexing fields
		table1.makeIndex(table1.attribute("Continent_Id"));
		table2.makeIndex(table2.attribute("Continent_Id"));
				
		// Report 
		System.out.println(table1+"\n");
		System.out.println(table2+"\n");
		for (Instances target : Arrays.asList(table1, table2)) 
			for (Instances source : Arrays.asList(table1, table2)) if ( target != source ) {
				for (String methodName : Arrays.asList("innerJoin","leftJoin","fullJoin")) {
					Method method = target.getClass().getMethod(methodName, Instances.class);
					result = (Instances) method.invoke(target,source);
					result.setRelationName(String.format("%s.%s(%s)", target.relationName(),methodName,source.relationName()));
					System.out.println(result+"\n");					
				}
			}				
	}

}
 
