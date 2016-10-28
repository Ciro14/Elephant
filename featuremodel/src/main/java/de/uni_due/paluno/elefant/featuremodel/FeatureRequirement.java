package de.uni_due.paluno.elefant.featuremodel;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by Ole Meyer
 */
public class FeatureRequirement {

    @NotEmpty
    private String feature_uuid;
    private boolean active;

    public String getFeature_uuid() {
        return feature_uuid;
    }

    public void setFeature_uuid(String feature_uuid) {
        this.feature_uuid = feature_uuid;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
