package com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves;

import com.example.olivo.bangmobile.gameMechanics.elements.cards.Card;

import java.util.ArrayList;

/**
 * Created by olivo on 15/02/2016.
 */
public class PickCardMove extends Move {
    public Move.Type type = Move.Type.PICKCARD;
    public PickType pickType;
    public int amountToGet;
    public boolean cardsChosen;
    public ArrayList<Card> cardsToGet;
    public ArrayList<Card> chosenCards;


    public enum PickType {
        THROW,
        HEALTHROW,
        PHASE1CHOOSE,
        MAGASIN,
        BRAQUAGE,
        COUPDEFOUDRE,
        DUEL,
        INDIENS,
        GATLING,
        BANG, THROWSHERIF, LUCKYDUKEDRAW, LUCKYDUKEDRAWDYNAMITE,
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
