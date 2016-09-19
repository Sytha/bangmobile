package com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves;

import com.example.olivo.bangmobile.gameMechanics.elements.cards.Card;

import java.util.ArrayList;

/**
 * Created by olivo on 12/02/2016.
 */
public class GetCardMove extends Move{
    public ArrayList<Card> cardToGet;


    public GetCardMove(ArrayList<Card> cardToGet){
        type = Type.GETCARD;
        this.cardToGet=cardToGet;
    }
}
