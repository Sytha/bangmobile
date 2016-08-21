package com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves;

import com.example.olivo.bangmobile.gameMechanics.elements.cards.Card;

import java.util.ArrayList;

/**
 * Created by olivo on 12/02/2016.
 */
public class PlayMove extends Move{
    public Type type = Type.PLAYCARD;

    public ArrayList<Card> availableCards;
    public ArrayList<Card> disabledCards;
    public Card playedCard;

    public PlayMove(ArrayList<Card> availableCards, ArrayList<Card> disabledCards){
        this.availableCards=availableCards;
        this.disabledCards=disabledCards;
    }

    public void Select(Card card){
        playedCard=card;
    }
}
