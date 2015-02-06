package weka.join.examples;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ExamplesReader {
	public static weka.core.Instances getInstances(String example) {
		String path = "/weka/join/examples/data/"+example;
		InputStreamReader input = new InputStreamReader(Testing.class.getClass().getResourceAsStream(path));
		BufferedReader reader = new BufferedReader(input);
		try {
			return new weka.core.Instances(reader);
		} catch (IOException e) {
			System.err.print(e.toString());
			return null;
		}
	}
}
