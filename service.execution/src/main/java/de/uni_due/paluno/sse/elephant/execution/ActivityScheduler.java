package de.uni_due.paluno.sse.elephant.execution;

import de.uni_due.paluno.elefant.featuremodel.Activity;
import de.uni_due.paluno.elefant.featuremodel.Feature;
import de.uni_due.paluno.elefant.featuremodel.FeatureRequirement;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Ole Meyer
 */
public class ActivityScheduler {
    public List<ActivityContainer> schedule(List<FeatureDiff> diffs, ModelStorage model){
        List<ActivityContainer> activities=new LinkedList<>();
        Hashtable<String,FeatureDiff> scheduledDiffs=new Hashtable<>();
        for(FeatureDiff diff:diffs){
            boolean noaction=true;
            for(Activity activity:diff.getDiffValue()?diff.getTarget().getActivateActivities():diff.getTarget().getDeactivateActivities()){
                activities.add(new ActivityContainer(activity,diff));
                noaction=false;
            }
            if(noaction)activities.add(new ActivityContainer(diff));
            //activities.addAll(diff.getDiffValue()?diff.getTarget().getActivateActivities():diff.getTarget().getTurnOffActivities());
            scheduledDiffs.put(diff.getTarget().getName(),diff);
        }
        resolveDependencies(activities,scheduledDiffs,model);

        return activities;
    }

    public void resolveDependencies(List<ActivityContainer> activities,Hashtable<String,FeatureDiff> scheduledDiffs, ModelStorage model){
        List<ActivityContainer> treatedActivities=new LinkedList<>();
        boolean newActivityFound=true;
        while(newActivityFound){
            newActivityFound=false;
            for(int i=0;i<activities.size();++i){
                ActivityContainer activity=activities.get(i);
                //Check if activity was already treated
                if(!treatedActivities.contains(activity)){
                    treatedActivities.add(activity);
                    if(activity.getActivity()!=null)for(FeatureRequirement fr:activity.getActivity().getFeatureRequirements()){
                        Feature  feature=model.getFeature(fr.getFeature_uuid());
                        if(feature==null)throw new RuntimeException("Can't resolve dependencies: Feature "+fr.getFeature_uuid()+" not found.");
                        if(feature.isActive()!=fr.isActive()){
                            //Check for a contradiction
                            FeatureDiff scheduledDiff=scheduledDiffs.get(feature.getName());
                            if(scheduledDiff!=null){
                                if(scheduledDiff.getDiffValue()!=fr.isActive()){
                                    throw new RuntimeException("Can'd decide if feature "+feature.getName()+" should be active or not.");
                                }
                            }else{
                                FeatureDiff ndiff=new FeatureDiff(feature,fr.isActive());
                                scheduledDiffs.put(feature.getName(),ndiff);
                                List<Activity> nactivities=fr.isActive()?feature.getActivateActivities():feature.getDeactivateActivities();
                                List<ActivityContainer> nacs=new LinkedList<>();
                                for(Activity a:nactivities)nacs.add(new ActivityContainer(a,ndiff));
                                activities.addAll(i,nacs);
                                newActivityFound=true;
                            }
                        }
                    }
                }
            }
        }
    }
}
