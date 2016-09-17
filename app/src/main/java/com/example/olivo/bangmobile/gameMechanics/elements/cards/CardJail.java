package com.example.olivo.bangmobile.gameMechanics.elements.cards;

import com.example.olivo.bangmobile.gameMechanics.Game;
import com.example.olivo.bangmobile.gameMechanics.elements.Figure;
import com.example.olivo.bangmobile.gameMechanics.elements.Player;
import com.example.olivo.bangmobile.gameMechanics.elements.Role;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.Action;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.Move;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.TargetMove;
import com.example.olivo.bangmobile.gameMechanics.interactions.infos.Info;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by olivo on 22/08/2016.
 *
 */
public class CardJail extends Card {
    Player source;
    Player target;
    JailState state;
    public enum JailState{
        TARGET,
        ONBOARD,
        QUICKDRAWPENDING
    }

    @Override
    public boolean usable(Player player, Game game) {
        for(Player p : game.players.values()){
            if(p != player && p.role != Role.SHERIF) return true;
        }
        return false;
    }

    @Override
    public void play(Player source, Game game) {
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
            this.target = tgMove.selectedPlayer;
            this.target.addBoardCard(this.source.removeBoardCard(this));
            game.interactionStack.addLast(new Info(source, Info.InfoType.JAILED, this.target));
            Figure.suziLafayetteAbility(game);
            state = JailState.ONBOARD;
            actionEnded=true;
        }else if(state == JailState.ONBOARD){
            game.quickDraw(this.target, new ArrayList<>(Arrays.asList(new Card.CardColor[]{Card.CardColor.HEART})), 1, 13);
            if(game.quickDrawPending){
                this.state = JailState.QUICKDRAWPENDING;
            }else{
                checkJail(!game.quickDrawResult, game);
            }
        }else if(state == JailState.QUICKDRAWPENDING){
            checkJail(!game.quickDrawResult, game);
        }
    }

    @Override
    public void reset(){
        actionEnded = false;
    }

    private void checkJail(boolean stayInJail, Game game){
        game.throwDeque.push(this.target.removeBoardCard(this));
        if(stayInJail){
            game.interactionStack.addLast(new Info(this.target, Info.InfoType.JAIL_STAY));
            game.state= Game.State.END;
        }else{
            game.interactionStack.addLast(new Info(this.target, Info.InfoType.JAIL_EVADE));
            game.state= Game.State.PHASE1;
        }
        this.source=null;
        this.target=null;
        this.state=null;
        actionEnded=true;
    }



}
