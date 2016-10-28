package de.uni_due.paluno.elefant.featuremodel;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Ole Meyer
 */
public class Activity {

    @Valid
    private List<FeatureRequirement> featureRequirements=new LinkedList<FeatureRequirement>();
    @Valid
    private List<AttributeRequirement> attributeRequirements=new LinkedList<AttributeRequirement>();
    @NotEmpty
    private String url;


    public List<FeatureRequirement> getFeatureRequirements() {
        return featureRequirements;
    }

    public void setFeatureRequirements(List<FeatureRequirement> featureRequirements) {
        this.featureRequirements = featureRequirements;
    }

    public List<AttributeRequirement> getAttributeRequirements() {
        return attributeRequirements;
    }

    public void setAttributeRequirements(List<AttributeRequirement> attributeRequirements) {
        this.attributeRequirements = attributeRequirements;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Activity{" +
                "url='" + url + '\'' +
                '}';
    }
}
