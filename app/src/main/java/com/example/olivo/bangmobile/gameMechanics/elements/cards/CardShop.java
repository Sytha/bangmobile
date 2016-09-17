package com.example.olivo.bangmobile.gameMechanics.elements.cards;

import com.example.olivo.bangmobile.gameMechanics.Game;
import com.example.olivo.bangmobile.gameMechanics.elements.Player;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.Action;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.Move;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.PickCardMove;
import com.example.olivo.bangmobile.gameMechanics.interactions.infos.Info;

import java.util.ArrayList;

/**
 * Created by olivo on 22/08/2016.
 *
 */
public class CardShop extends Card {
    ArrayList<Card> cardsToGet;
    Player source;
    Player currentCustomer;

    @Override
    public void play(Player source, Game game) {
        this.source=source;
        game.interactionStack.addLast(new Info(this.source, Info.InfoType.CARDSHOP));
        cardsToGet = new ArrayList<>();
        for(int i = 0;i<game.players.size();i++){
            cardsToGet.add(game.cardDeque.pop());
        }
        currentCustomer=source;
        askPlayerToChoose(currentCustomer,game);
    }

    @Override
    public void action(Player source, Move move, Game game) {
        PickCardMove pMove = (PickCardMove) move;
        cardsToGet.remove(pMove.chosenCards.get(0));
        source.handCards.add(pMove.chosenCards.get(0));
        game.interactionStack.add(new Info(currentCustomer, Info.InfoType.SHOPPICKED));
        currentCustomer=currentCustomer.nextPlayer;
        askPlayerToChoose(currentCustomer,game);
    }

    private void askPlayerToChoose(Player player, Game game){
        if(cardsToGet.size()>0){
            ArrayList<Move> moveList = new ArrayList<>();
            moveList.add(new PickCardMove(cardsToGet,1, PickCardMove.PickType.SHOP));
            game.interactionStack.addFirst(new Action(player, moveList));
        }else{
            actionEnded=true;
        }
    }
}
