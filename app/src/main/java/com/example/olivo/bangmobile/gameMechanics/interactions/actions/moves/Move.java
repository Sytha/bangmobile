package com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves;

/**
 * Created by olivo on 09/02/2016.
 *
 */
public abstract class Move {
    public Type type;

    public enum Type {
        CHOICE("move.moveType.choice"),
        PASS("move.moveType.pass"),
        PLAYCARD("move.moveType.playCard"),
        GETCARD("move.moveType.getCard"),
        PICKCARD("move.moveType.pickCard"),
        TARGET("move.moveType.target"),
        SPECIAL("move.moveType.special");

        final String name;

        Type(String s){
            name=s;
        }

    }

}