package elefant.FMevolution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConfigurationValidator {

	public static String formatConfiguration(String configuration){
		List<String> configAsList = Arrays.asList(configuration.split(","));
		List<String> configAsListTrimed = new ArrayList<String>();
		for (String string : configAsList) {
			configAsListTrimed.add(string.trim().replaceAll("[^a-zA-Z0-9]", ""));
		}

		Object[] arrayTrimedAndCleaned = (Object[]) configAsListTrimed.toArray();
		Arrays.sort(arrayTrimedAndCleaned);

		String formattedConfiguration = (String) arrayTrimedAndCleaned[0];
		int arrayLength = arrayTrimedAndCleaned.length;
		if (arrayLength > 1) {
			for (int i = 1; i < arrayLength; i++) {
				formattedConfiguration += ",";
				formattedConfiguration += arrayTrimedAndCleaned[i];
			}
		}	
		return formattedConfiguration;
	}
	
	public static String formatConfiguration(List<String> configuration){
		List<String> configAsListTrimed = new ArrayList<String>();
		for (String string : configuration) {
			configAsListTrimed.add(string.trim().replaceAll("[^a-zA-Z0-9]", ""));
		}

		Object[] arrayTrimedAndCleaned = (Object[]) configAsListTrimed.toArray();
		Arrays.sort(arrayTrimedAndCleaned);

		String formattedConfiguration = (String) arrayTrimedAndCleaned[0];
		int arrayLength = arrayTrimedAndCleaned.length;
		if (arrayLength > 1) {
			for (int i = 1; i < arrayLength; i++) {
				formattedConfiguration += ",";
				formattedConfiguration += arrayTrimedAndCleaned[i];
			}
		}	
		return formattedConfiguration;
	}

	public static List<String> getConfigAsList(String configuration){
		return Arrays.asList(configuration.split(","));
	}
}

