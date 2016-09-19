package com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves;

/**
 * Created by olivo on 12/02/2016.
 */
public class PassMove extends Move {
    public PassReason reason;

    public PassMove(PassReason reason){
        type = Type.PASS ;
        this.reason = reason;
    }

    public enum PassReason {
        ENDTURN("move.passReason.endTurn"),
        ENDLIFE("move.passReason.endLife"),
        BANGPASS("move.passReason.bangPass"),
        DUELPASS("move.passReason.duelPass"),
        APACHEPASS("move.passReason.apachePass"),
        GATLINGPASS("move.passReason.gatlingPass");

        final String name;

        PassReason(String s){
            name=s;
        }
    }
}
