package com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves;

import com.example.olivo.bangmobile.gameMechanics.elements.cards.Card;

import java.util.ArrayList;

/**
 * Created by olivo on 15/02/2016.
 *
 */
public class PickCardMove extends Move {
    public PickType pickType;
    public int amountToGet;
    public boolean cardsChosen;
    public ArrayList<Card> cardsToGet;
    public ArrayList<Card> chosenCards;


    public enum PickType {
        THROW("move.pickType.throw"),
        KITCARLSONPHASE1("move.pickType.kitCarlsonPhase1"),
        SHOP("move.pickType.shop"),
        ROBBERY("move.pickType.robbery"),
        LOVESTRIKE("move.pickType.loveStrike"),
        DEFDUEL("move.pickType.defDuel"),
        DEFAPACHE("move.pickType.defApache"),
        DEFGATLING("move.pickType.defGatling"),
        DEFBANG("move.pickType.defBang"),
        THROWSHERIF("move.pickType.throwSherif"),
        LUCKYDUKEABILITY("move.pickType.luckyDukeAbility"),
        SIDKETCHUMABILITY("move.pickType.sidKetchumAbility"),
        SAVEBEER("move.pickType.saveBeer");

        final String name;

        PickType(String s){
            name=s;
        }
    }

    public PickCardMove(ArrayList<Card> cardsToGet, int amountToGet,PickType pickType ) {
        type=Move.Type.PICKCARD;
        this.amountToGet = amountToGet;
        this.cardsToGet = cardsToGet;
        cardsChosen = false;
        this.pickType = pickType;
    }

    public void chooseCards(ArrayList<Card> chosenCards){
        this.chosenCards = chosenCards;
        cardsChosen=true;
    }
}
