package de.uni_due.paluno.elefant.featuremodel;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Ole Meyer
 *         This class describes the connection between a parent and a quantity of child features.
 *         All variants could be represented through the use of the min and the max field.
 *         OR: min=1 and max>min
 *         AND: min=max
 *         XOR: min=1 and max=min
 *         OTHER: min=x and max=y for a selection of x up to y features
 */
public class FeatureConnection {

    @Min(0)
    private int min;
    @Min(1)
    private int max;

    @NotEmpty
    @Valid
    private List<Feature> features = new LinkedList<>();

    public List<Feature> getFeatures() {
        return features;
    }

    public void setFeatures(List<Feature> features) {
        this.features = features;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }
}
