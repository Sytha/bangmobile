package com.example.olivo.bangmobile.gameMechanics.elements.cards;

import com.example.olivo.bangmobile.gameMechanics.Game;
import com.example.olivo.bangmobile.gameMechanics.elements.Figure;
import com.example.olivo.bangmobile.gameMechanics.elements.Player;
import com.example.olivo.bangmobile.gameMechanics.interactions.infos.Info;

/**
 * Created by olivo on 22/08/2016.
 * 
 */
public class CardVolcanic extends Card {
    @Override
    public boolean usable(Player player, Game game) {
        return(!player.hasCardOnBoard(this.id));
    }

    @Override
    public void play(Player source, Game game) {
        game.throwDeque.push(source.removeBoardCard(source.hasWeaponOnBoard()));
        source.addBoardCard(source.removeHandCard(this));
        game.interactionStack.addLast(new Info(source, Info.InfoType.CARDVOLCANIQUE));
        Figure.suziLafayetteAbility(game);
    }

    @Override
    public void addBoardCardEffect(Player player) {
        player.unlimitedBang = true;
    }

    @Override
    public void removeBoardCardEffect(Player player) {
        if(player.figure.id != Figure.fig_id.WILLY_THE_KID){
            player.unlimitedBang = false;
        }
    }
}
