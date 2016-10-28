package de.uni_due.paluno.elefant.featuremodel;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Ole Meyer
 *         This class describes a single feature of the system
 */
public class Feature {

    @NotEmpty
    private String id;
    @NotEmpty
    private String name;
    private boolean active;
    @Valid
    private List<FeatureConnection> featureConnections = new LinkedList<FeatureConnection>();
    @Valid
    private List<Attribute> attributes = new LinkedList<Attribute>();
    @Valid
    private List<Activity> activateActivities = new LinkedList<Activity>();
    @Valid
    private List<Activity> deactivateActivities = new LinkedList<Activity>();
    @Valid
    private List<Softgoal> softgoals=new LinkedList<>();
    @Valid
    private List<SoftgoalImpact> softgoalImpacts=new LinkedList<>();


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }


    public void setActive(boolean active) {
        this.active = active;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<FeatureConnection> getFeatureConnections() {
        return featureConnections;
    }

    public void setFeatureConnections(List<FeatureConnection> featureConnections) {
        this.featureConnections = featureConnections;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    public List<Activity> getActivateActivities() {
        return activateActivities;
    }

    public void setActivateActivities(List<Activity> activateActivities) {
        this.activateActivities = activateActivities;
    }

    public List<Activity> getDeactivateActivities() {
        return deactivateActivities;
    }

    public void setDeactivateActivities(List<Activity> deactivateActivities) {
        this.deactivateActivities = deactivateActivities;
    }

    public List<Softgoal> getSoftgoals() {
        return softgoals;
    }

    public void setSoftgoals(List<Softgoal> softgoals) {
        this.softgoals = softgoals;
    }

    public List<SoftgoalImpact> getSoftgoalImpacts() {
        return softgoalImpacts;
    }

    public void setSoftgoalImpacts(List<SoftgoalImpact> softgoalImpacts) {
        this.softgoalImpacts = softgoalImpacts;
    }
}
