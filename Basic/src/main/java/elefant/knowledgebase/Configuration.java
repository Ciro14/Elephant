package elefant.knowledgebase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.bson.Document;


public class Configuration {

	ArrayList<String> features = new ArrayList<>();
	
	public Configuration(String jArray_configuration)
	{
		StringTokenizer st = new StringTokenizer(jArray_configuration, "\"");		
		while(st.hasMoreTokens())
		{
			String element = st.nextToken();
			if (element.compareTo("[")!=0&&element.compareTo("]")!=0&&element.compareTo(",")!=0) 
				features.add(element);
		}
		features = sort(features);
	}

	private ArrayList<String> sort(ArrayList<String> array) {

		ArrayList<String> sorted_array = new ArrayList<>();
		
		for (int i = 0; i < array.size(); i++) {
			String temp = array.get(i);
			for (int j = i+1; j < array.size(); j++) {
				if (array.get(j).compareTo(temp)<0) {
					temp = array.get(j);
				}
			}
			sorted_array.add(temp);
		}
		return sorted_array;
	}
	
	public String compact_sorted_string()
	{
		String compact_form="";
		for (int i = 0; i < features.size(); i++) {
			if (compact_form.compareTo("")==0) compact_form+="[";
				
			compact_form += "\"" + features.get(i) + "\"";
			
			if ((i+1)==features.size())  compact_form+="]";
			else compact_form += ",";
		}
		return compact_form;
	}

	public ArrayList<String> toJsonArray() {

		return features;
		
	}
	
/*	public String toJsonArray() {

		String output = "[";
		
		for (String element : features) {
			if (output.compareTo("[")!=0) output+=",";
				
			output += "\"" + element + "\""; 
		}
		
		output+="]";
		return output;
		
	}
	*/
}
