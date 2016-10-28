package de.uni_due.paluno.sse.elephant.execution;

import com.google.gson.Gson;
import de.uni_due.paluno.elefant.featuremodel.Feature;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Created by Ole Meyer
 */

@RestController
public class RESTController {


    private ModelStorage modelStorage=new ModelStorage();
    private DiffCreator diffCreator=new DiffCreator();
    private ActivityScheduler activityScheduler=new ActivityScheduler();
    private ActivityExecutor activityExecutor=new ActivityExecutor();

    @RequestMapping(value = "/model", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void setModel(@Valid @RequestBody Feature rootFeature){
        modelStorage.setRootFeature(rootFeature);
    }

    @RequestMapping(value = "/model/update", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ActivityContainer>updateModel(@Valid @RequestBody Feature rootFeature, @RequestParam(required = false, value = "execute") boolean execute){
        List<FeatureDiff> featureDiffs=diffCreator.extractFeatureDiffs(modelStorage,new ModelStorage(rootFeature));
        List<ActivityContainer> activities=activityScheduler.schedule(featureDiffs,modelStorage);
        if(execute)activityExecutor.execute(activities);
        return activities;
    }

    @RequestMapping(value = "/model", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<ActivityContainer> updateModelSimple(@Valid @RequestBody String[] ids){
        //TODO find a better way to copy the feature model
        Gson gson=new Gson();
        ModelStorage ms=new ModelStorage(gson.fromJson(gson.toJson(modelStorage.getRootFeature()),Feature.class));
        for(Feature feature:ms.getAllFeatures()){
            feature.setActive(false);
        }
        for(String id:ids){
            Feature feature=ms.getFeature(id);
            if(feature!=null)feature.setActive(true);
        }
        return updateModel(ms.getRootFeature(),true);
    }

}
