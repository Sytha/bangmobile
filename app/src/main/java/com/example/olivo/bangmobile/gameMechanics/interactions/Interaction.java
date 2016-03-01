package com.example.olivo.bangmobile.gameMechanics.interactions;

/**
 * Created by olivo on 23/02/2016.
 */
public abstract class Interaction {
    public Types type;
    public Destinataires destinataires;

    public enum Types{
        INFO,
        ACTION
    }

    public enum Destinataires{
        ALL,
        OTHER,
        SELF
    }
}
