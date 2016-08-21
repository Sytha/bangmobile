package com.example.olivo.bangmobile.gameMechanics.elements.cards;

import com.example.olivo.bangmobile.gameMechanics.Game;
import com.example.olivo.bangmobile.gameMechanics.elements.Figure;
import com.example.olivo.bangmobile.gameMechanics.elements.Player;
import com.example.olivo.bangmobile.gameMechanics.interactions.Interaction;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.Move;
import com.example.olivo.bangmobile.gameMechanics.interactions.infos.Info;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;

public class Dynamite extends Card implements ICard {
    Player source;


    public boolean usable(Player source, ArrayList<Player> playerList){
        return true;
    }

    @Override
    public void play(Player source, ArrayList<Player> targetsList, Game game) {
        this.source = source;
        source.addBoardCard(source.removeHandCard(this));
        game.interactionStack.addLast(new Info(source, Info.InfoType.CARDDYNAMITE));
    }

    @Override
    public void resume(Move move, Game game) {
        if(source.figure.id == Figure.fig_id.LUCKY_DUKE){
            //::game.quickDrawLuckyDuck();
        }else{
        }
    }

    public void play(Player source, ArrayList<Player> playerList, Deque<Interaction> interactionStack){

    }

    private void resumeDynamite(Game game){
        boolean detonate = false;

        if(game.quickDraw(new ArrayList<Card.CardColor>(Arrays.asList(new Card.CardColor[]{Card.CardColor.PIKE})), 2, 9)) {
            detonate =  true;
        }

        if(detonate){
            game.currentPlayer.healthPoint-=3;
            game.currentPlayer.removeBoardCard(Card_id.DYNAMITE);
            game.interactionStack.addLast(new Info(game.currentPlayer, Info.InfoType.DYNAMITE_EXPLODED));
            if(!game.checkMort(game.currentPlayer)){
                game.drawBartCassidy(game.currentPlayer, 3);
            }
        }else {
            game.interactionStack.addLast(new Info(game.currentPlayer, Info.InfoType.DYNAMITE_THROWED));
            game.currentPlayer.nextPlayer.addBoardCard(game.currentPlayer.removeBoardCard(this));
        }
    }

    private void resumeLuckyDukeDynamite(){

    }
}
