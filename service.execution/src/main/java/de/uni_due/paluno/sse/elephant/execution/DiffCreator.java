package de.uni_due.paluno.sse.elephant.execution;

import de.uni_due.paluno.elefant.featuremodel.Feature;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Ole Meyer
 */
public class DiffCreator {
    public List<FeatureDiff> extractFeatureDiffs(ModelStorage oldModel, ModelStorage newModel){
        List<FeatureDiff> diffs=new LinkedList<>();
        for(Feature feature:newModel.getAllFeatures()){
            Feature oldFeature=oldModel.getFeature(feature.getId());
            if(oldFeature!=null){
                if(oldFeature.isActive()!=feature.isActive())diffs.add(new FeatureDiff(oldFeature,feature.isActive()));
            }
        }
        return diffs;
    }
}
