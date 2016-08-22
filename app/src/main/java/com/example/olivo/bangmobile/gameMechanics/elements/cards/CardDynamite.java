package com.example.olivo.bangmobile.gameMechanics.elements.cards;

import com.example.olivo.bangmobile.gameMechanics.Game;
import com.example.olivo.bangmobile.gameMechanics.elements.Figure;
import com.example.olivo.bangmobile.gameMechanics.elements.Player;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.Move;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.PickCardMove;
import com.example.olivo.bangmobile.gameMechanics.interactions.infos.Info;

import java.util.ArrayList;
import java.util.Arrays;

public class CardDynamite extends Card {
    Player source;
    DynamiteState dynState;

    public CardDynamite(){
        super();
        dynState = DynamiteState.PENDING;
    }

    public enum DynamiteState {
        PENDING,
        LUCKYDUKE,
    }


    public boolean usable(Player source, Game game){
        return true;
    }

    @Override
    public void play(Player source, ArrayList<Player> targetsList, Game game) {
        source.addBoardCard(source.removeHandCard(this));
        game.interactionStack.addLast(new Info(source, Info.InfoType.CARDDYNAMITE));
    }

    @Override
    public void action(Player source, Move move, Game game) {
        this.source = source;
        if(dynState == DynamiteState.PENDING){
            if(this.source.figure.id == Figure.fig_id.LUCKY_DUKE){
                game.quickDrawLuckyDuck();
                dynState = DynamiteState.LUCKYDUKE;
            }else{
                simpleActionDynamite(game);
            }
        }else if(dynState == DynamiteState.LUCKYDUKE){
            luckyDukeActionDynamite((PickCardMove) move,game);
            dynState = DynamiteState.PENDING;
        }
    }

    private void simpleActionDynamite(Game game){
        boolean detonate = false;

        if(game.quickDraw(new ArrayList<>(Arrays.asList(new Card.CardColor[]{Card.CardColor.PIKE})), 2, 9)) {
            detonate =  true;
        }

        checkDetonate(detonate,game);
    }

    private void luckyDukeActionDynamite(PickCardMove move, Game game){
        Card chosenCard = move.chosenCards.get(0);
        checkDetonate((chosenCard.cardColor == CardColor.PIKE && chosenCard.cardValue >=2 && chosenCard.cardValue >= 9),game);
        game.resumeQuickDrawLuckyDuck(move);
    }

    private void checkDetonate(boolean detonate,Game game){
        if(detonate){
            source.healthPoint-=3;
            source.removeBoardCard(Card_id.DYNAMITE);
            game.interactionStack.addLast(new Info(source, Info.InfoType.DYNAMITE_EXPLODED));
            if(!game.checkMort(source)){
                game.drawBartCassidy(source, 3);
            }
        }else {
            game.interactionStack.addLast(new Info(source, Info.InfoType.DYNAMITE_THROWED));
            source.nextPlayer.addBoardCard(source.removeBoardCard(this));
        }
    }


}
