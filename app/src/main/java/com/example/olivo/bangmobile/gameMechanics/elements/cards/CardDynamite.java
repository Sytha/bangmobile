package com.example.olivo.bangmobile.gameMechanics.elements.cards;

import com.example.olivo.bangmobile.gameMechanics.Game;
import com.example.olivo.bangmobile.gameMechanics.elements.Figure;
import com.example.olivo.bangmobile.gameMechanics.elements.Player;
import com.example.olivo.bangmobile.gameMechanics.elements.Turn;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.Move;
import com.example.olivo.bangmobile.gameMechanics.interactions.infos.Info;

import java.util.ArrayList;
import java.util.Arrays;

public class CardDynamite extends Card {
    Player source;
    DynamiteState dynState;

    public CardDynamite(){
        super();

    }

    public enum DynamiteState {
        ONBOARD,
        QUICKDRAWPENDING,
        DYING
    }

    @Override
    public boolean usable(Player source, Game game){
        return(!source.hasCardOnBoard(Card_id.DYNAMITE));
    }

    @Override
    public void play(Player source, ArrayList<Player> targetsList, Game game) {
        source.addBoardCard(source.removeHandCard(this));
        game.interactionStack.addLast(new Info(source, Info.InfoType.CARDDYNAMITE));
        Figure.checkSuziLafayette(game);
        dynState = DynamiteState.ONBOARD;
        actionEnded = true;
    }

    @Override
    public void action(Player source, Move move, Game game) {
        this.source = source;
        actionEnded = false;
        if(dynState == DynamiteState.ONBOARD){
            game.quickDraw(source, new ArrayList<>(Arrays.asList(new Card.CardColor[]{Card.CardColor.PIKE})), 2, 9);
            if(game.quickDrawPending){
                dynState = DynamiteState.QUICKDRAWPENDING;
            }else{
                checkDetonate(game.quickDrawResult,game);
            }
        }else if(dynState == DynamiteState.QUICKDRAWPENDING){
            checkDetonate(game.quickDrawResult,game);
            dynState = DynamiteState.ONBOARD;
        }else if(dynState == DynamiteState.DYING){
            if(source.healthPoint>0){
                Figure.drawBartCassidy(source, 3,game);
                actionEnded = true;
                game.currentTurn.state = Turn.State.JAIL;
            }
        }
    }

    private void checkDetonate(boolean detonate,Game game){
        if(detonate){
            source.healthPoint-=3;
            game.throwDeque.push(source.removeBoardCard(this));
            if(game.isDying(source)){
                game.interactionStack.addFirst(new Info(source, Info.InfoType.DYNAMITE_EXPLODED));
                dynState=DynamiteState.DYING;
            } else {
                Figure.drawBartCassidy(source, 3,game);
                actionEnded = true;
                game.currentTurn.state = Turn.State.JAIL;
            }
        }else {
            game.interactionStack.addLast(new Info(source, Info.InfoType.DYNAMITE_THROWED));
            source.nextPlayer.addBoardCard(source.removeBoardCard(this));
            actionEnded = true;
            game.currentTurn.state = Turn.State.JAIL;
        }
    }
}
