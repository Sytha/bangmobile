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
        this.info = info;
    }

    public enum InfoType {
        START,
        PLAY,
        CHECK_DYNAMITE,
        CHECK_JAIL,
        DYNAMITE_THROWED,
        DYNAMITE_EXPLODED,
        WIN,
        DYING,
        DIE,
        END,

    }
}
