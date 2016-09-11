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
public class CardBeer extends Card {
    @Override
    public void play(Player source, ArrayList<Player> targetsList, Game game) {
        game.throwDeque.push(source.removeHandCard(this));
        Figure.checkSuziLafayette(game);
        game.interactionStack.addLast(new Info(source, Info.InfoType.CARDBEER));
        if(game.players.size() <= 2){
            game.interactionStack.addLast(new Info(source, Info.InfoType.BEERUSELESS2P));
        }else if(source.healthPoint == source.maxHealthPoint){
            game.interactionStack.addLast(new Info(source, Info.InfoType.BEERUSELESSMAXHEALTH));
        }else{
            game.interactionStack.addLast(new Info(source, Info.InfoType.BEERHEAL));
            source.healthPoint++;
        }
        actionEnded = true;
    }
}
