package com.example.olivo.bangmobile.gameMechanics.elements.cards;

import com.example.olivo.bangmobile.gameMechanics.Game;
import com.example.olivo.bangmobile.gameMechanics.elements.Figure;
import com.example.olivo.bangmobile.gameMechanics.elements.Player;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.Move;
import com.example.olivo.bangmobile.gameMechanics.interactions.infos.Info;

import java.util.ArrayList;

/**
 * Created by olivo on 22/08/2016.
 */
public class CardScope extends Card {
    @Override
    public boolean usable(Player player, Game game) {
        return(!player.hasCardOnBoard(this.id));
    }

    @Override
    public void play(Player source, ArrayList<Player> targetsList, Game game) {
        source.addBoardCard(source.removeHandCard(this));
        game.interactionStack.addLast(new Info(source, Info.InfoType.CARDSCOPE));
        Figure.checkSuziLafayette(game);
    }

    @Override
    public void addBoardCardEffect(Player player) {
        player.vision+=1;
    }

    @Override
    public void removeBoardCardEffect(Player player) {
        player.vision-=1;
    }
}
