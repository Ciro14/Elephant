package de.uni_due.paluno.elefant.featuremodel;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by Ole Meyer
 */
public class AttributeRequirement {

    @NotEmpty
    private String feature_uuid;
    @NotEmpty
    private String attribute_uuid;
    private double min;
    private double max;

    public String getFeature_uuid() {
        return feature_uuid;
    }

    public void setFeature_uuid(String feature_uuid) {
        this.feature_uuid = feature_uuid;
    }

    public String getAttribute_uuid() {
        return attribute_uuid;
    }

    public void setAttribute_uuid(String attribute_uuid) {
        this.attribute_uuid = attribute_uuid;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }
}
