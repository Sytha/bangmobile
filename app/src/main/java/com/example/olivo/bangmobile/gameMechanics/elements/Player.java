package com.example.olivo.bangmobile.gameMechanics.elements;

import java.util.ArrayList;
import java.util.Random;
import com.example.olivo.bangmobile.gameMechanics.elements.Card.Card_id;

/**
 * Created by olivo on 05/01/2016.
 */
public class Player {
    public int id;
    public String name;
    public Role role;
    public Figure figure;
    public ArrayList<Card> handCards;
    public ArrayList<Card> boardCards;
    public int maxHealthPoint;
    public int healthPoint;
    public int evasion = 0;
    public int vision = 1;
    public int weaponVision = 1;
    public Player nextPlayer;
    public Player prevPlayer;

    public Card_id hasWeaponOnBoard(){
        for(Card c : boardCards){
            if(c.type == Card.WEAPON) return c.id;
        }
        return null;
    }

    public boolean hasCardOnBoard(Card_id idCard){
        for(Card card : boardCards){
            if(card.id == idCard){
                return true;
            }
        }
        return false;
    }

    public void addBoardCard(Card card){
        boardCards.add(card);
        switch(card.id){
            case MUSTANG :
                evasion += 1;
                break;
            case LUNETTE :
                vision += 1;
                break;
            case SCHOFIELD :
                weaponVision = 2;
                break;
            case REMINGTON :
                weaponVision = 3;
                break;
            case CARABINE:
                weaponVision = 4;
                break;
            case WINCHESTER:
                weaponVision = 5;
                break;
        }
    }

    public Card removeBoardCard(Card_id idCard){
        Card removedCard = null;
        for(Card card : boardCards){
            if(card.id == idCard){
                boardCards.remove(card);
                removedCard = card;
                switch(card.id){
                    case MUSTANG :
                        evasion -= 1;
                        break;
                    case LUNETTE :
                        vision -= 1;
                        break;
                    case SCHOFIELD :
                        weaponVision = 1;
                        break;
                    case REMINGTON :
                        weaponVision = 1;
                        break;
                    case CARABINE:
                        weaponVision = 1;
                        break;
                    case WINCHESTER:
                        weaponVision = 1;
                        break;
                }
            }
        }
        return removedCard;
    }

    public boolean hasCardInHand(Card_id idCard){
        for(Card card : handCards){
            if(card.id == idCard){
                return true;
            }
        }
        return false;
    }

    public boolean hasAmountOfCardInHand(Card_id idCard, int amount){
        int actualNumber = 0;
        for(Card card : handCards){
            if(card.id == idCard){
                actualNumber++;
            }
        }
        return(actualNumber>=amount);
    }

    public void addHandCard(Card card){
        handCards.add(card);
    }

    public Card removeHandCard(Card_id idCard){
        Card removedCard = null;
        for(Card card : handCards){
            if(card.id == idCard){
                handCards.remove(card);
                removedCard = card;
            }
        }
        return removedCard;
    }

    public Card removeRandomHandCard(){
        Random rand = new Random();
        int randNumber = rand.nextInt(handCards.size());
        return(handCards.remove(randNumber));
    }


}
