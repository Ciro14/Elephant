package de.uni_due.paluno.elefant.featuremodel;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * @author Ole Meyer
 *         The class describes an attribute of a feature
 */
public class Attribute {
    @NotEmpty
    private String uuid;
    @NotEmpty
    private String name;
    private double value;
    private double min;
    private double max;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }


}
