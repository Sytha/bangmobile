package com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves;

import com.example.olivo.bangmobile.gameMechanics.elements.Card;

import java.util.ArrayList;

/**
 * Created by olivo on 15/02/2016.
 */
public class PickCardMove extends Move {
    Move.Type type = Move.Type.PICKCARD;
    PickType pickType;
    int amountToGet;
    boolean cardsChosen;
    ArrayList<Card> cardsToGet;
    ArrayList<Card> chosenCards;


    public enum PickType {
        THROW,
        SAVETHROW,
        PHASE1CHOOSE,
        SHOP
    }
    public PickCardMove(ArrayList<Card> cardsToGet, int amountToGet,PickType pickType ) {
        this.cardsToGet = cardsToGet;
        cardsChosen = false;
        this.pickType = pickType;
    }

    public void chooseCards(ArrayList<Card> chosenCards){
        this.chosenCards = chosenCards;
        cardsChosen=true;
    }
}
