package de.uni_due.paluno.sse.elephant.service.monitoring.controller;

import de.uni_due.paluno.sse.elephant.service.monitoring.db.MeasurementRepository;
import de.uni_due.paluno.sse.elephant.service.monitoring.model.Measurement;
import de.uni_due.paluno.sse.elephant.service.monitoring.model.Metric;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

/**
 * @author Ole Meyer
 */
@Component
public class MeasurementThread extends Timer {

    private final Map<Integer,MeasurementThread> timerMap=new HashMap<>();
    private final Map<String,MeasurementThread> idToThread=new HashMap<>();


    @Autowired
    private MeasurementRepository measurementRepository;


    private TimerTask timerTask;
    private Timer timer;
    private List<Metric> metrics=new LinkedList<>();

    public MeasurementThread(){}
    public MeasurementThread(MeasurementRepository measurementRepository, int intervall){
        System.out.println("Create new thread for intervall "+intervall);
        this.measurementRepository=measurementRepository;
        timerTask=new TimerTask() {
            @Override
            public void run() {
                long time=System.currentTimeMillis();
                for(Metric metric:metrics){
                    System.out.println("Measure "+metric.getId()+" at time "+time);
                    URL url = null;
                    try {
                        url = new URL(metric.getUrl());
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        connection.setDoOutput(true);
                        InputStream content = (InputStream)connection.getInputStream();
                        BufferedReader br=new BufferedReader(new InputStreamReader(content));
                        String response=br.readLine();
                        br.close();
                        double val=Double.valueOf(response);
                        Measurement measurement=new Measurement(metric.getId(),val,time);
                        measurementRepository.insert(measurement);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        timer=new Timer();
        timer.scheduleAtFixedRate(timerTask,intervall,intervall);
    }



    public void measure(Metric metric){
        if(timerMap.get(metric.getInterval())==null){
            timerMap.put(metric.getInterval(),
                    new MeasurementThread(measurementRepository,metric.getInterval()));
        }

        timerMap.get(metric.getInterval()).addMetric(metric);
    }

    public void addMetric(Metric metric){
        System.out.println("Add metric "+metric.getId()+" to timer "+this);
        if(idToThread.get(metric.getId())!=null)idToThread.get(metric.getId()).removeMetric(metric);
        idToThread.put(metric.getId(),this);
        this.metrics.add(metric);

    }

    public void removeMetric(Metric metric){
        this.metrics.remove(metric);
    }

    public List<Metric> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<Metric> metrics) {
        this.metrics = metrics;
    }
}
