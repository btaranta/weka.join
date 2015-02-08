package weka.join.examples;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Random;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.join.*;

public class PerformanceCheck {

	
	public static void main(String[] args) throws Exception {

		weka.join.Instances target;
		weka.join.Instances source;
		int targetSize = 10000;
		int sourceSize = 10;
				
		FastVector attrs;		
		
		attrs = new FastVector();		
		attrs.addElement(new Attribute("Id"));
		attrs.addElement(new Attribute("SomeNumber"));
		target = new Instances(new weka.core.Instances("Target", attrs, targetSize));
		
		attrs = new FastVector();		
		attrs.addElement(new Attribute("Id"));
		attrs.addElement(new Attribute("OtherNumber"));
		source = new Instances(new weka.core.Instances("Source", attrs, sourceSize));
		
		Random generator = new Random();
		
		for (double n=0; n<targetSize; n++) 			
			target.add(new Instance(1.0, new double[]{n, generator.nextDouble()}));
		
		for (double n=0; n<sourceSize; n++)
			source.add(new Instance(1.0, new double[]{generator.nextInt(sourceSize), generator.nextDouble()}));
			
		target.makeIndex(target.attribute("Id"));
		
		long startTime;
		long endTime;
		
		for (Instances table : Arrays.asList(target, source)) 
			System.out.println(String.format("%s relation has %d rows and %d columns.", table.relationName(), table.numInstances(), table.numAttributes()));
		
				
		startTime = System.nanoTime();
		source.makeIndex(source.attribute("Id"));
		endTime = System.nanoTime();		
		System.out.println(String.format("\nBuilding index on %s relation : %f ms\n", source.relationName(), ((double)(endTime-startTime))/1000000.0 ));
		
		Instances result;
		Method method;
		
		for (String methodName : Arrays.asList("innerJoin","leftJoin","fullJoin","update")) {		 
				 
			method = target.getClass().getMethod(methodName, Instances.class);
			startTime = System.nanoTime();
			result = (Instances) method.invoke(target,source);
			endTime = System.nanoTime();	
			System.out.println(String.format("Performing %s using index: %f ms", methodName, ((double)(endTime-startTime))/1000000.0 ));
			
			method = target.getClass().getMethod(methodName, Instances.class, Attribute.class);
			startTime = System.nanoTime();
			result = (Instances) method.invoke(target,source,target.attribute("Id"));
			endTime = System.nanoTime();	
			System.out.println(String.format("Performing %s using full scan: %f ms\n", methodName, ((double)(endTime-startTime))/1000000.0 ));
			
		}
	}

}
