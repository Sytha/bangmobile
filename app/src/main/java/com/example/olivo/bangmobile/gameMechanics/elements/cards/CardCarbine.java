package com.example.olivo.bangmobile.gameMechanics.elements.cards;

import com.example.olivo.bangmobile.gameMechanics.Game;
import com.example.olivo.bangmobile.gameMechanics.elements.Figure;
import com.example.olivo.bangmobile.gameMechanics.elements.Player;
import com.example.olivo.bangmobile.gameMechanics.interactions.infos.Info;

/**
 * Created by olivo on 22/08/2016.
 *
 */
public class CardCarbine extends Card {
    @Override
    public boolean usable(Player player, Game game) {
        return(!player.hasCardOnBoard(this.id));
    }

    @Override
    public void play(Player source, Game game) {
        if(source.hasWeaponOnBoard() != null){
            game.throwDeque.push(source.removeBoardCard(source.hasWeaponOnBoard()));
        }
        Figure.suziLafayetteAbility(game);
        source.addBoardCard(source.removeHandCard(this));
        game.interactionStack.addLast(new Info(source, Info.InfoType.CARDCARBINE));
        actionEnded = true;
    }

    @Override
    public void addBoardCardEffect(Player player) {
        player.weaponVision=3;
    }

    @Override
    public void removeBoardCardEffect(Player player) {
        player.weaponVision=0;
    }
}
