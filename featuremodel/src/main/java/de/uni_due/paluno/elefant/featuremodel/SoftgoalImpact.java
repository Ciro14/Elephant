package de.uni_due.paluno.elefant.featuremodel;

import javax.validation.constraints.NotNull;

/**
 * @author Ole Meyer
 */
public class SoftgoalImpact {
    @NotNull
    private String uuid;
    private double impact;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public double getImpact() {
        return impact;
    }

    public void setImpact(double impact) {
        this.impact = impact;
    }
}
