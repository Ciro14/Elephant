package de.uni_due.paluno.sse.elephant.service.monitoring.model;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * @author Ole Meyer
 */
@Document(collection = "measurements")
public class Measurement {
    @Field("metricid")
    private String metricid;
    @Field("value")
    private double value;
    @Field("time")
    private long time;

    public Measurement(String metricid, double value, long time) {
        this.metricid=metricid;
        this.value = value;
        this.time=time;
    }

    public String getMetricid() {
        return metricid;
    }

    public void setMetricid(String metricid) {
        this.metricid = metricid;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
