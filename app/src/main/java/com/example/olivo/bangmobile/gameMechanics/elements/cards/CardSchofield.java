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
public class CardSchofield extends Card {
    @Override
    public boolean usable(Player player, Game game) {
        return(!player.hasCardOnBoard(this.id));
    }

    @Override
    public void play(Player source, ArrayList<Player> targetsList, Game game) {
        game.throwDeque.push(source.removeBoardCard(source.hasWeaponOnBoard()));
        source.addBoardCard(source.removeHandCard(this));
        game.interactionStack.addLast(new Info(source, Info.InfoType.CARDSCHOFIELD));
        Figure.checkSuziLafayette(game);
    }

    @Override
    public void action(Player source, Move move, Game game) {

    }

    @Override
    public void addBoardCardEffect(Player player) {
        player.weaponVision = 1;
    }

    @Override
    public void removeBoardCardEffect(Player player) {
        player.weaponVision = 0;
    }
}
