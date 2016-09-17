package com.example.olivo.bangmobile.gameMechanics.elements.cards;

import com.example.olivo.bangmobile.gameMechanics.Game;
import com.example.olivo.bangmobile.gameMechanics.elements.Player;
import com.example.olivo.bangmobile.gameMechanics.interactions.infos.Info;

/**
 * Created by olivo on 22/08/2016.
 *
 */
public class CardSaloon extends Card {

    @Override
    public void play(Player source, Game game) {
        game.interactionStack.addLast(new Info(source, Info.InfoType.CARDSALOON));
        for(Player player : game.players.values()){
            if(player.healthPoint < player.maxHealthPoint){
                player.healthPoint++;
                game.interactionStack.addLast(new Info(player, Info.InfoType.SALOONHEAL));
            }else{
                game.interactionStack.addLast(new Info(player, Info.InfoType.SALOONUSELESSHEAL));
            }
        }
    }

}
