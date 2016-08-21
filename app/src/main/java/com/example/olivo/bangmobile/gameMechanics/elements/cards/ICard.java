package com.example.olivo.bangmobile.gameMechanics.elements.cards;

import com.example.olivo.bangmobile.gameMechanics.Game;
import com.example.olivo.bangmobile.gameMechanics.elements.Player;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.Move;

import java.util.ArrayList;

/**
 * Created by Sytha on 19/08/2016.
 */
public interface ICard {

    public boolean usable(Player player, ArrayList<Player> playerList);

    public void play(Player source, ArrayList<Player> targetsList, Game game);

    public void resume(Move move, Game game);

}
