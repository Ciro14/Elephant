package de.uni_due.paluno.sse.elephant.wakeup;

import okhttp3.*;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by Ole on 23.10.2016.
 */
public class ModelTransferREST extends Thread {
    private String url;
    private String filename;

    public ModelTransferREST(String url, String filename) {
        this.url = url;
        this.filename = filename;
    }

    @Override
    public void run() {
        System.out.println(this.getId()+"\t => \t"+"Post model to service "+url);
        OkHttpClient client = new OkHttpClient();

        byte[] model= new byte[0];
        try {
            System.out.println(this.getId()+"\t => \t"+"Read model from "+filename);
            model = FileUtils.readFileToByteArray(new File(filename));
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, model);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .addHeader("content-type", "application/json")
                    .addHeader("cache-control", "no-cache")
                    .build();

            System.out.println(this.getId()+"\t => \t"+"Make HTTP call");
            Response response = client.newCall(request).execute();
            System.out.println(this.getId()+"\t => \t"+"Response Code was "+response.code());
            if(response.code()!=200)throw new RuntimeException("Model from file "+filename+" could not send to "+url+". Response Code was "+response.code()+", body was "+response.message());
            response.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
