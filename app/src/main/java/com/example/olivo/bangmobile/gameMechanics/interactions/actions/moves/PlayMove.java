package com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves;

import com.example.olivo.bangmobile.gameMechanics.elements.Card;

import java.util.ArrayList;

/**
 * Created by olivo on 12/02/2016.
 */
public class PlayMove extends Move{
    public Type type = Type.PLAYCARD;

    public ArrayList<Card> availableCards;
    public Card playedCard;

    public void PlayMove(ArrayList<Card> availableCards){
        this.availableCards=availableCards;
    }

    public void Select(Card card){
        playedCard=card;
    }
}
