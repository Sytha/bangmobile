package com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves;

/**
 * Created by olivo on 03/03/2016.
 *
 */
public class SpecialMove extends Move {
    public Ability ability;

    public SpecialMove(Ability ability){
        this.ability=ability;
        type=Type.SPECIAL;
    }

    public enum Ability{
        JOURDONNAISABILITY("move.ability.jourdonnaisAbility"),
        HIDEOUT("move.ability.hideOut"),
        SIDKETCHUMABILITY("move.ability.sidKetchumAbility"),
        SAVEBEER("move.ability.saveBeer");

        final String name;

        Ability(String s){
            name=s;
        }
    }
}
