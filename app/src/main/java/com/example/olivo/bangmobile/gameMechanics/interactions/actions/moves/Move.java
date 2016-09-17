package com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves;

/**
 * Created by olivo on 09/02/2016.
 *
 */
public abstract class Move {
    public Type type;

    public enum Type {
        CHOICE,
        PASS,
        PLAYCARD,
        GETCARD,
        PICKCARD,
        TARGET,
        SPECIAL
    }

}