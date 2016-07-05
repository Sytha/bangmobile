package com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves;

/**
 * Created by olivo on 03/03/2016.
 */
public class SpecialMove extends Move {
    public Ability ability;



    public enum Ability{
        MISSTOBANG,
        BANGTOMISS,
        THROWTOLIFE
    }
}
