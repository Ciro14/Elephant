package de.uni_due.paluno.sse.elephant.execution;

import okhttp3.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

/**
 * Created by Ole Meyer
 */
public class ActivityExecutor {
    public void execute(List<ActivityContainer> activities){
        RestTemplate restTemplate=new RestTemplate();
        for(ActivityContainer activity:activities){
            System.out.println("Start reconfiguration of Feature "+ activity.getDiff().getTarget().getId());

            if(activity.getActivity()!=null){

                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url(activity.getActivity().getUrl())
                        .get()
                        .addHeader("cache-control", "no-cache")
                        .build();

                Response response = null;
                try {
                    response = client.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(response.code()==200){
                    activity.getDiff().getTarget().setActive(activity.getDiff().getDiffValue());
                    System.out.println("Feature "+ activity.getDiff().getTarget().getId()+" was reconfigured.");
                }else{
                    throw new RuntimeException("Can't reconfigure. Response Code was "+response.code()+", message was "+response.message());
                }
                response.close();
            }else{
                activity.getDiff().getTarget().setActive(activity.getDiff().getDiffValue());
                System.out.println("Feature "+ activity.getDiff().getTarget().getId()+" was reconfigured silently..");

            }
        }


    }
}
