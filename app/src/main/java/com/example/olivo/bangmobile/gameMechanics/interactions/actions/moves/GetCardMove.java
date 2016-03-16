package com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves;

import com.example.olivo.bangmobile.gameMechanics.elements.Card;

import java.util.ArrayList;

/**
 * Created by olivo on 12/02/2016.
 */
public class GetCardMove extends Move{
    Type type = Type.GETCARD;
    boolean cardReclaimed;
    ArrayList<Card> cardToGet;


    public GetCardMove(ArrayList<Card> cardToGet){
        this.cardToGet=cardToGet;
        cardReclaimed=false;
    }

    public ArrayList<Card> reclaimCards(){
        cardReclaimed=false;
        return cardToGet;
    }
}
