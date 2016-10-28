package de.uni_due.paluno.sse.elephant.service.monitoring.model;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;


/**
 * @author Ole Meyer
 */
public class Metric {
    @NotEmpty
    private String id;
    @NotEmpty
    private String url;
    @Min(0)
    private int interval;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    @Override
    public String toString() {
        return "Metric{" +
                "id='" + id + '\'' +
                ", url='" + url + '\'' +
                ", interval=" + interval +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Metric metric = (Metric) o;

        return id != null ? id.equals(metric.id) : metric.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
