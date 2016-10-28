package de.uni_due.paluno.sse.elefant.FMevolution;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import es.us.isa.FAMA.Reasoner.Question;
import es.us.isa.FAMA.Reasoner.QuestionTrader;
import es.us.isa.FAMA.Reasoner.questions.ProductsQuestion;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.GenericFeatureModel;
import es.us.isa.FAMA.models.featureModel.Product;
import es.us.isa.FAMA.models.variabilityModel.GenericProduct;

public class FeatureModelManager {

	public static List<List<String>> getAllProducts(String fmPath) {
		List<List<String>> products = new ArrayList<List<String>>();
		QuestionTrader qt = new QuestionTrader();
		GenericFeatureModel fm = (GenericFeatureModel) qt.openFile(fmPath);
		qt.setVariabilityModel(fm);
		Question q = qt.createQuestion("Products");
		qt.ask(q);
		ProductsQuestion pq = (ProductsQuestion) q;
		Iterator<? extends GenericProduct> it = pq.getAllProducts().iterator();
		while (it.hasNext()){
			List<String> product = new ArrayList<String>();
			Product p = (Product) it.next();
			Iterator<GenericFeature> it2 = p.getFeatures().iterator();
			while (it2.hasNext()){
				product.add(it2.next().getName());
			}
			products.add(product);
		}
		for (List<String> list : products) {
			for (String string : list) {
				System.out.print(string + " ");
			}
			System.out.println();
		}
		return products;
	}

	public static List<List<String>> getInvalidConfigurations(List<List<String>> productsFM, List<List<String>> productsFM_evo) {
		List<List<String>> invalidConfigs = new ArrayList<List<String>>();
		if (productsFM_evo.containsAll(productsFM)) {
			// There are new configurations (e.g., add feature), but no invalid ones
		} else {
			if (containsAll(productsFM, productsFM_evo)) {
				// There are invalid configurations	
				invalidConfigs = pruneConfigs(productsFM, productsFM_evo);
			} else {
				// There are invalid configurations, but there are also new ones
			}
		}
		return invalidConfigs;
	}
	
	/**
	 * Checks if @param productsFM contains all elements of @param productsFM_evo 
	 * @param productsFM
	 * @param productsFM_evo
	 * @return
	 */
	private static boolean containsAll(List<List<String>> productsFM, List<List<String>> productsFM_evo) {
		for (List<String> products_evo : productsFM_evo) {
			boolean validProduct = false;
			for (List<String> products : productsFM) {
				if (products_evo.containsAll(products)) {
					validProduct = true;
				}
			}
			if (!validProduct) {
				return false;
			} 
		}
		return true;
	}
	
	/**
	 * Get the list of products that are in @param productsFM but not in @param productsFM_evo 
	 * @param productsFM
	 * @param productsFM_evo
	 * @return
	 */
	private static List<List<String>> pruneConfigs(List<List<String>> productsFM, List<List<String>> productsFM_evo) {
		List<List<String>> prunedConfigs = new ArrayList<List<String>>();
		for (List<String> products : productsFM) {
			boolean validProduct = false;
			for (List<String> products_evo : productsFM_evo) {
				if (products_evo.containsAll(products)) {
					validProduct = true;
				}
			}
			if (!validProduct) {
				prunedConfigs.add(products);
			} 
		}
		return prunedConfigs;
	}
}
