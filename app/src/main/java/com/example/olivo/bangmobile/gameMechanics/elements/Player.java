package com.example.olivo.bangmobile.gameMechanics.elements;

import java.util.ArrayList;
import java.util.Random;

import com.example.olivo.bangmobile.gameMechanics.elements.cards.Card;
import com.example.olivo.bangmobile.gameMechanics.elements.cards.Card.Card_id;

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
    public int weaponVision = 0;
    public Player nextPlayer;
    public Player prevPlayer;
    public boolean unlimitedBang = false;

    public Player(int id, String name) {
        this.id = id;
        this.name = name;
        this.handCards = new ArrayList<>();
        this.boardCards = new ArrayList<>();
    }

    public Card hasWeaponOnBoard(){
        for(Card c : boardCards){
            if(c.type == Card.Card_type.WEAPON) return c;
        }
        return null;
    }

    public boolean hasBoardCard(Card_id id){
        for(Card c : boardCards){
            if(c.id == id) return true;
        }
        return false;
    }

    public Card getCardFromBoard(Card_id id){
        for(Card c : boardCards){
            if(c.id == id) return c;
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
        card.addBoardCardEffect(this);
    }


    public Card removeBoardCard(Card toRemoveCard){
        Card removedCard = null;
        for(Card card : boardCards){
            if(card == toRemoveCard){
                boardCards.remove(card);
                removedCard = card;
            }
        }
        removedCard.removeBoardCardEffect(this);
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

    public Card removeHandCard(Card card){
        this.handCards.remove(card);
        return card;
    }

    public Card removeHandCard(int cardUniqueId){
        Card removedCard = null;
        for(Card card : handCards){
            if(card.uniqueID == cardUniqueId){
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

    public boolean canSteal(){
        Player nextPlayer = this.nextPlayer;
        Player prevPlayer = this.prevPlayer;
        int distance=1;
        while(distance <= vision){
            if((nextPlayer.evasion+distance)<=vision || (prevPlayer.evasion+distance)<=vision){
                return true;
            }
            if(nextPlayer == prevPlayer || (nextPlayer.nextPlayer == prevPlayer && prevPlayer.prevPlayer == nextPlayer)){
                break;
            }
            nextPlayer = nextPlayer.nextPlayer;
            prevPlayer = prevPlayer.prevPlayer;
            distance++;
        }

        return false;
    }

    public ArrayList<Player> getAvailableTarget(int playerVision,int maxDistance){
        ArrayList<Player> targetsAvailable = new ArrayList<>();
        Player nextPlayer = this.nextPlayer;
        Player prevPlayer = this.prevPlayer;
        for(int distance = 1 ;distance<=maxDistance && distance <=playerVision ;distance++){
            if((nextPlayer.evasion + distance) <= playerVision ){
                targetsAvailable.add(nextPlayer);
                nextPlayer = nextPlayer.nextPlayer;
            }
            if(nextPlayer!=prevPlayer && (prevPlayer.evasion + distance) <= playerVision){
                targetsAvailable.add(prevPlayer);
                prevPlayer = prevPlayer.prevPlayer;
            }
        }
        return targetsAvailable;
    }

    public boolean canBang(int maxDistance){
        ArrayList<Player> targetList = this.getAvailableTarget(this.vision + this.weaponVision, maxDistance);
        return( targetList.size() > 0);
    }

    public ArrayList<Player> getAllOtherTarget(){
        ArrayList<Player> playerList =  new ArrayList<>();
        Player nPlayer = nextPlayer;
        while(nPlayer != this){
            playerList.add(nPlayer);
            nPlayer = nPlayer.nextPlayer;
        }
        return playerList;
    }

    public boolean canBang(){
        Player nextPlayer = this.nextPlayer;
        Player prevPlayer = this.prevPlayer;
        int distance=1;
        while(distance <= vision+weaponVision){
            if((nextPlayer.evasion+distance)<=vision || (prevPlayer.evasion+distance)<=vision){
                return true;
            }
            if(nextPlayer == prevPlayer || (nextPlayer.nextPlayer == prevPlayer && prevPlayer.prevPlayer == nextPlayer)){
                break;
            }
            nextPlayer = nextPlayer.nextPlayer;
            prevPlayer = prevPlayer.prevPlayer;
            distance++;
        }

        return false;
    }


    public boolean canCoupDeFoudre(){
        if(this.boardCards.size()>0 || this.handCards.size()>1)return true;

        Player player = this.nextPlayer;
        while(player.id != this.id){
            if(player.boardCards.size()>0 || player.handCards.size()>1)return true;
            player = this.nextPlayer;
        }
        return false;
    }
}
