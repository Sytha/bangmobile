package com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves;

import com.example.olivo.bangmobile.gameMechanics.elements.Player;

import java.util.ArrayList;

/**
 * Created by olivo on 12/02/2016.
 */
public class TargetMove extends Move {
    public Type type = Type.TARGET;
    public ArrayList<Player> availableTargets;
    public Player selectedPlayer;
    public TargetType targetType;

    public enum TargetType{
        BANG,
        STEAL,
        BRAQUAGE,
        COUPDEFOUDRE,
        DUEL,
        JAIL
    }

    public TargetMove(ArrayList<Player> availableTargets, TargetType targetType){
        this.availableTargets = availableTargets;
        this.targetType=targetType;
    }

    public void selectTarget(Player player){
        selectedPlayer=player;
    }
}
