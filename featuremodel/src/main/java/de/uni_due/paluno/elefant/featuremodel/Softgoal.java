package de.uni_due.paluno.elefant.featuremodel;

import javax.validation.constraints.NotNull;

/**
 * Created by Ole Meyer
 */
public class Softgoal {
    @NotNull
    private String uuid;
    @NotNull
    private String name;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
