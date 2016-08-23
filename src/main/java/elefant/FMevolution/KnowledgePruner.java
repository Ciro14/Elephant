package elefant.FMevolution;

import java.util.List;

public class KnowledgePruner {

	public static void main(String[] args) {
		String pathFM = "./fms/original.fm";
		String pathFM_evo = "./fms/evo.fm";
		List<List<String>> productsFM = FeatureModelManager.getAllProducts(pathFM);
		List<List<String>> productsFM_evo = FeatureModelManager.getAllProducts(pathFM_evo);
		List<List<String>> invalidConfigurations = FeatureModelManager.getInvalidConfigurations(productsFM, productsFM_evo);
		
		for (List<String> list : invalidConfigurations) {
			System.out.println(list);
		}
		
		MongoManager manager = new MongoManager();
		manager.pruneKnowledge(invalidConfigurations);
	}

}
