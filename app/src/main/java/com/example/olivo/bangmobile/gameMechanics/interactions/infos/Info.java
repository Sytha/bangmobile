package com.example.olivo.bangmobile.gameMechanics.interactions.infos;

import com.example.olivo.bangmobile.gameMechanics.elements.Player;
import com.example.olivo.bangmobile.gameMechanics.interactions.Interaction;

/**
 * Created by olivo on 23/02/2016.
 */
public class Info extends Interaction {
    public Player player;
    public InfoType info;


    public Info(Player player, InfoType info){
        this.player=player;
        this.info = info;
    }

    public enum InfoType {
        START,
        CHECK_DYNAMITE,
        CHECK_JAIL,
        JAIL_EVADE,
        JAIL_STAY,
        DYNAMITE_THROWED,
        DYNAMITE_EXPLODED,
        PHASE1,
        PHASE1STEAL,
        PHASE1TRASH,
        PHASE1BONUS,
        PHASE1CHOOSE,
        PHASE2PLAY,
        WIN,
        DYING,
        DIE,
        END,

    }
}
