package com.example.olivo.bangmobile.gameMechanics.elements.cards;

import com.example.olivo.bangmobile.gameMechanics.Game;
import com.example.olivo.bangmobile.gameMechanics.elements.Player;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.Move;

import java.util.ArrayList;

/**
 * Created by olivo on 22/08/2016.
 */
public class CardCarbine extends Card {
    @Override
    public boolean usable(Player player, Game game) {
        return false;
    }

    @Override
    public void play(Player source, ArrayList<Player> targetsList, Game game) {

    }

    @Override
    public void action(Player source, Move move, Game game) {

    }
}
