package com.example.olivo.bangmobile.gameMechanics.elements.cards;

import com.example.olivo.bangmobile.gameMechanics.Game;
import com.example.olivo.bangmobile.gameMechanics.elements.Figure;
import com.example.olivo.bangmobile.gameMechanics.elements.Player;
import com.example.olivo.bangmobile.gameMechanics.interactions.infos.Info;

/**
 * Created by olivo on 22/08/2016.
 *
 */
public class CardBeer extends Card {
    @Override
    public void play(Player source, Game game) {
        game.throwDeque.push(source.removeHandCard(this));
        Figure.suziLafayetteAbility(game);
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
