package de.uni_due.paluno.elefant.featuremodel;

/**
 * Created by User on 29.06.2016.
 */
public class Configration extends Feature {
    private static int lastid=0;

    private int id;

    public Configration() {
        lastid++;
        id+=lastid;
    }
}
