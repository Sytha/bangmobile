package com.example.olivo.bangmobile.gameMechanics.interactions.actions;

import com.example.olivo.bangmobile.gameMechanics.elements.Player;
import com.example.olivo.bangmobile.gameMechanics.interactions.Interaction;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.Move;

import java.util.ArrayList;

/**
 * Created by olivo on 04/02/2016.
 */
public class Action extends Interaction {
    public Player player;
    public ArrayList<Move> availableMoves;
    public Move selectedMove;

    public Action(Player player, ArrayList<Move> availableMoves) {
        this.player = player;
        this.availableMoves = availableMoves;
        this.type = Types.ACTION;
    }

    public void selectMove(Move move){
        selectedMove = move;
    }
}
