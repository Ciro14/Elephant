package de.uni_due.paluno.sse.elephant.execution;

import de.uni_due.paluno.elefant.featuremodel.Activity;

/**
 * Created by Ole Meyer
 */
public class ActivityContainer {
    private Activity activity;
    private FeatureDiff diff;

    public ActivityContainer(Activity activity, FeatureDiff diff) {
        this.activity = activity;
        this.diff = diff;
    }
    public ActivityContainer(FeatureDiff diff) {
        this.diff = diff;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }


    public FeatureDiff getDiff() {
        return diff;
    }

    public void setDiff(FeatureDiff diff) {
        this.diff = diff;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ActivityContainer that = (ActivityContainer) o;

        return activity != null ? activity.equals(that.activity) : that.activity == null;

    }

    @Override
    public int hashCode() {
        return activity != null ? activity.hashCode() : 0;
    }
}
