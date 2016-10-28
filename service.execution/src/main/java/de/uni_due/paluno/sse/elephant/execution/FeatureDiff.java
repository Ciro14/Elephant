package de.uni_due.paluno.sse.elephant.execution;

import de.uni_due.paluno.elefant.featuremodel.Feature;

/**
 * Created by Ole Meyer
 */
public class FeatureDiff extends Diff<Feature,Boolean> {

    public FeatureDiff(Feature target, Boolean diffValue) {
        super(target, diffValue);
    }



}
