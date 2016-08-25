package com.example.olivo.bangmobile.gameMechanics.elements.cards;

import com.example.olivo.bangmobile.gameMechanics.Game;
import com.example.olivo.bangmobile.gameMechanics.elements.Figure;
import com.example.olivo.bangmobile.gameMechanics.elements.Player;
import com.example.olivo.bangmobile.gameMechanics.elements.Role;
import com.example.olivo.bangmobile.gameMechanics.elements.Turn;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.Action;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.Move;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.PickCardMove;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.TargetMove;
import com.example.olivo.bangmobile.gameMechanics.interactions.infos.Info;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by olivo on 22/08/2016.
 */
public class CardJail extends Card {
    Player source;
    JailState state;
    public enum JailState{
        TARGET,
        ONBOARD,
        ONBOARDLUCKYDUKE
    }

    @Override
    public boolean usable(Player player, Game game) {
        for(Player p : game.players.values()){
            if(p != player && p.role != Role.SHERIF) return true;
        }
        return false;
    }

    @Override
    public void play(Player source, ArrayList<Player> targetsList, Game game) {
        ArrayList<Player> availableTargets = new ArrayList<>();
        for(Player p : game.players.values()){
            if(p != source && p.role != Role.SHERIF){
                availableTargets.add(p);
            }
        }

        ArrayList<Move> moveList = new ArrayList<>(Arrays.asList(new Move[]{new TargetMove(availableTargets, TargetMove.TargetType.JAIL)}));

        game.interactionStack.addLast(new Info(source, Info.InfoType.CARDJAIL));
        game.interactionStack.addLast(new Action(source, moveList));

        this.source=source;
        state=JailState.TARGET;
    }

    @Override
    public void action(Player source, Move move, Game game) {
        if(state == JailState.TARGET){
            TargetMove tgMove = (TargetMove) move;
            tgMove.selectedPlayer.addBoardCard(source.removeBoardCard(this));
            game.interactionStack.addLast(new Info(source, Info.InfoType.JAILED, tgMove.selectedPlayer));
            state = JailState.ONBOARD;
        }else if(state == JailState.ONBOARD){
            if(source.figure.id == Figure.fig_id.LUCKY_DUKE){
                game.quickDrawLuckyDuck();
                state = JailState.ONBOARDLUCKYDUKE;
            }else{
                checkJail(source, !game.quickDraw(new ArrayList<>(Arrays.asList(new Card.CardColor[]{Card.CardColor.HEART})), 1, 13), game);
            }
        }else if(state == JailState.ONBOARDLUCKYDUKE){
            checkJail(source, !game.resumeQuickDrawLuckyDuck((PickCardMove) move, new ArrayList<>(Arrays.asList(new Card.CardColor[]{Card.CardColor.HEART})), 1, 13), game);
        }
    }

    private void checkJail(Player source, boolean jailed, Game game){
        if(!jailed){
            game.interactionStack.addLast(new Info(source, Info.InfoType.JAIL_EVADE));
            game.currentTurn.state= Turn.State.PHASE1;
        }else{
            game.interactionStack.addLast(new Info(source, Info.InfoType.JAIL_STAY));
            game.currentTurn.state= Turn.State.END;
        }
    }
}
