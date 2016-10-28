package de.uni_due.paluno.sse.elephant.wakeup;


import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import okhttp3.*;
import org.apache.commons.io.FileUtils;
import org.bson.Document;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.Semaphore;

public class Main
{

    public static final String appDir="/app", modelDir="/models", monitorDir="/services/monitoring", executionDir="/services/execution", analyzerDir="/services/analyzer";

    public static Process app,monitor,analyzer,executor;

    public static void main( String[] args ) throws Exception {
        try{
            System.out.println("Wake up the Elephant!");

            //######################## MongoDB #############################
            JsonElement jsonElement=new Gson().fromJson(new FileReader(System.getProperty("user.dir")+modelDir+"/core.model"),JsonElement.class);
            JsonObject jsonObject=jsonElement.getAsJsonObject();
            MongoClient mongoClient=new MongoClient("localhost" , 27017);
            mongoClient.dropDatabase("elephant");
            MongoDatabase db=mongoClient.getDatabase("elephant");
            for(Map.Entry<String,JsonElement> entry:jsonObject.entrySet()){
                db.createCollection(entry.getKey());
                MongoCollection<Document> dbCollection=db.getCollection(entry.getKey());
                for(JsonElement element:entry.getValue().getAsJsonArray()){
                    Document doc=Document.parse(element.toString());
                    dbCollection.insertOne(doc);
                }
            }
            mongoClient.close();

            //######################## App #################################

            File[] appCandidates=new File(System.getProperty("user.dir")+appDir).listFiles(new FileFilter() {
                public boolean accept(File pathname) {
                    return pathname.getName().endsWith(".jar");
                }
            });

            if(appCandidates.length==0){
                System.out.println("Could'nt find an app. Please copy the jar file to "+System.getProperty("user.dir")+appDir+" if you did'nt want to start it by your self.");
            }else{
                File appFile=appCandidates[0];
                app=Runtime.getRuntime().exec("java -jar "+appFile.getAbsolutePath());
                System.out.println("java -jar "+appFile.getAbsolutePath());
                new SpringOutputReader("App",app,new Semaphore(0)).start();
            }

            //#################### Monitor ################################

            File[] monitorCandidates=new File(System.getProperty("user.dir")+monitorDir).listFiles(new FileFilter() {
                public boolean accept(File pathname) {
                    return pathname.getName().endsWith(".jar");
                }
            });

            if(monitorCandidates.length==0)throw new RuntimeException("Could'nt find the monitoring service.");

            File monitorFile=monitorCandidates[0];

            monitor=Runtime.getRuntime().exec("java -jar "+monitorFile.getAbsolutePath()+" --port=54783");

            Semaphore monitorSemaphore=new Semaphore(0);
            SpringOutputReader monitorReader=new SpringOutputReader("monitor",monitor,monitorSemaphore);
            monitorReader.start();

            monitorSemaphore.acquire();
            System.out.println("Monitor started");


            new ModelTransferREST("http://localhost:54783/metrics",System.getProperty("user.dir")+modelDir+"/sensor.model").start();

            //#################### Execution ################################

            File[] executionCandidates=new File(System.getProperty("user.dir")+executionDir).listFiles(new FileFilter() {
                public boolean accept(File pathname) {
                    return pathname.getName().endsWith(".jar");
                }
            });

            if(executionCandidates.length==0)throw new RuntimeException("Could'nt find the execution service.");

            File executionFile=executionCandidates[0];

            executor=Runtime.getRuntime().exec("java -jar "+executionFile.getAbsolutePath()+" --port=54784");

            Semaphore executorSemaphore=new Semaphore(0);
            new SpringOutputReader("executor",executor,executorSemaphore).start();
            executorSemaphore.acquire();
            System.out.println("Executor started");

            new ModelTransferREST("http://localhost:54784/model",System.getProperty("user.dir")+modelDir+"/feature.model").start();

            //#################### Analyzer & Planner ################################

            File[] analyzerCandidates=new File(System.getProperty("user.dir")+analyzerDir).listFiles(new FileFilter() {
                public boolean accept(File pathname) {
                    return pathname.getName().endsWith(".jar");
                }
            });

            if(analyzerCandidates.length==0)throw new RuntimeException("Could'nt find the analyzer service.");

            File analyzerFile=analyzerCandidates[0];

            analyzer=Runtime.getRuntime().exec("java -jar "+analyzerFile.getAbsolutePath()+" http://localhost:54784/model [\"screensaver\",\"quality\"]");
           // System.out.println("java -jar "+analyzerFile.getAbsolutePath()+" http://localhost:54784/model [\"screensaver\",\"quality\"]");

            new SpringOutputReader("analyzer",analyzer,new Semaphore(0)).start();
            System.out.println("Analyzer started");

            monitorReader.setPrint(false);


            BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
            while(!br.readLine().contains("bed"));
        }catch(Exception e){
            throw e;
        }finally {
            if(app!=null)app.destroy();
            if(monitor!=null)monitor.destroy();
            if(executor!=null)executor.destroy();
            if(analyzer!=null)analyzer.destroy();
            System.out.println("Good night!");
        }


    }
}
