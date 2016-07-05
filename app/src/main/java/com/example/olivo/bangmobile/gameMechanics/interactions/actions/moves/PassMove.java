package com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves;

/**
 * Created by olivo on 12/02/2016.
 */
public class PassMove extends Move {
    public Type type= Type.PASS;
    public PassReason reason;

    public PassMove(PassReason reason){
        this.reason = reason;
    }

    public enum PassReason {
        ENDTURN,
        ENDBANG,
        ENDDUEL,
        ENDLIFE,
        FAILINDIENS,
        FAILGATLING,
    }
}
