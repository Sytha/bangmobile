package com.example.olivo.bangmobile.gameMechanics.elements.cards;

import com.example.olivo.bangmobile.gameMechanics.Game;
import com.example.olivo.bangmobile.gameMechanics.elements.Figure;
import com.example.olivo.bangmobile.gameMechanics.elements.Player;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.Move;
import com.example.olivo.bangmobile.gameMechanics.interactions.infos.Info;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by olivo on 22/08/2016.
 */
public class CardHideOut extends Card {
    boolean quickDrawPending = false;

    @Override
    public boolean usable(Player player, Game game) {
        return(!player.hasCardOnBoard(this.id));
    }

    @Override
    public void play(Player source, ArrayList<Player> targetsList, Game game) {
        source.addBoardCard(source.removeHandCard(this));
        game.interactionStack.addLast(new Info(source, Info.InfoType.CARDHIDEOUT));
        Figure.checkSuziLafayette(game);
    }

    @Override
    public void action(Player source, Move move, Game game) {
        game.interactionStack.addLast(new Info(source, Info.InfoType.HIDEOUTQUICKDRAW));
        game.quickDraw(source, new ArrayList<>(Arrays.asList(new Card.CardColor[]{Card.CardColor.HEART})), 1, 13);
    }

}
