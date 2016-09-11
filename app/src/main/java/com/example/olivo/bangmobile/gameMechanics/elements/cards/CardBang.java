package com.example.olivo.bangmobile.gameMechanics.elements.cards;

import com.example.olivo.bangmobile.gameMechanics.Game;
import com.example.olivo.bangmobile.gameMechanics.elements.Figure;
import com.example.olivo.bangmobile.gameMechanics.elements.Player;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.Action;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.Move;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.TargetMove;
import com.example.olivo.bangmobile.gameMechanics.interactions.infos.Info;

import java.util.ArrayList;

/**
 * Created by olivo on 22/08/2016.
 */
public class CardBang extends Card {
    Player source;

    @Override
    public boolean usable(Player player, Game game) {
        return (player.canBang((int)Math.floor((double) game.players.values().size()/2)) || game.currentTurn.bangUsed);
    }

    @Override
    public void play(Player source, ArrayList<Player> targetsList, Game game) {
        this.source=source;
        game.interactionStack.addFirst(new Info(source, Info.InfoType.CARDBANG));
        ArrayList<Move> moveList = new ArrayList<>();
        moveList.add(new TargetMove(source.getAvailableTarget(source.vision + source.weaponVision, (int) Math.floor((double)game.players.values().size()/2)), TargetMove.TargetType.BANG));
        game.interactionStack.add(new Action(source,moveList));
        if(!source.unlimitedBang){
            game.currentTurn.bangUsed = true;
        }
    }

    @Override
    public void action(Player source, Move move, Game game) {

    }
}
