package com.example.olivo.bangmobile.gameMechanics.elements.cards;

import com.example.olivo.bangmobile.gameMechanics.Game;
import com.example.olivo.bangmobile.gameMechanics.elements.Player;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.Action;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.GetCardMove;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.Move;
import com.example.olivo.bangmobile.gameMechanics.interactions.infos.Info;

import java.util.ArrayList;

/**
 * Created by olivo on 22/08/2016.
 *
 */
public class CardConvoy extends Card {

    @Override
    public void play(Player source, Game game) {
        game.throwDeque.push(source.removeHandCard(this));
        game.interactionStack.addLast(new Info(source, Info.InfoType.CARDCONVOY));
        source.handCards.add(game.getCardFromDeque());
        source.handCards.add(game.getCardFromDeque());
        actionEnded = true;
    }

}
