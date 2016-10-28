package de.uni_due.paluno.sse.elephant.execution;

import de.uni_due.paluno.elefant.featuremodel.Feature;
import de.uni_due.paluno.elefant.featuremodel.FeatureConnection;

import java.util.Collection;
import java.util.Hashtable;

/**
 * Created by Ole Meyer
 */
public class ModelStorage {
    private Feature rootFeature;
    private Hashtable<String,Feature> uuidToFeature=new Hashtable<>();

    public ModelStorage(Feature rootFeature) {
        this.rootFeature = rootFeature;
        index();
    }

    public ModelStorage() {
    }

    public Feature getFeature(String uuid){
        return uuidToFeature.get(uuid);
    }
    public Collection<Feature> getAllFeatures(){
        return uuidToFeature.values();
    }

    public Feature getRootFeature() {
        return rootFeature;
    }

    public void setRootFeature(Feature rootFeature) {
        this.rootFeature = rootFeature;
        index();

    }

    private void index(){
        uuidToFeature.clear();
        recursiveIndex(rootFeature);
    }

    private void recursiveIndex(Feature feature){
        uuidToFeature.put(feature.getId(),feature);
        for(FeatureConnection fc:feature.getFeatureConnections()){
            for(Feature f:fc.getFeatures())recursiveIndex(f);
        }
    }
}
