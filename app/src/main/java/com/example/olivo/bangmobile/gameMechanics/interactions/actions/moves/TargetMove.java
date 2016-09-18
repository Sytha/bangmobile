package com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves;

import com.example.olivo.bangmobile.gameMechanics.elements.Player;

import java.util.ArrayList;

/**
 * Created by olivo on 12/02/2016.
 *
 */
public class TargetMove extends Move {
    public Type type = Type.TARGET;
    public ArrayList<Player> availableTargets;
    public Player selectedPlayer;
    public Target target;

    public enum Target {
        BANG("move.target.bang"),
        JESSEJONESPHASE1("move.target.jesseJonesPhase1"),
        ROBBERY("move.target.robbery"),
        LOVESTRIKE("move.target.loveStrike"),
        DUEL("move.target.duel"),
        JAIL("move.target.jail");

        final String name;

        Target(String s){
            name=s;
        }
    }

    public TargetMove(ArrayList<Player> availableTargets, Target target){
        this.availableTargets = availableTargets;
        this.target = target;
    }

    public void selectTarget(Player player){
        selectedPlayer=player;
    }
}
