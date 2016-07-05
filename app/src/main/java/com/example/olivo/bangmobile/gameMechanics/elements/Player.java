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
    public int weaponVision = 0;
    public Player nextPlayer;
    public Player prevPlayer;

    public Card hasWeaponOnBoard(){
        for(Card c : boardCards){
            if(c.type == Card.Card_type.WEAPON) return c;
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
                weaponVision = 1;
                break;
            case REMINGTON :
                weaponVision = 2;
                break;
            case CARABINE:
                weaponVision = 3;
                break;
            case WINCHESTER:
                weaponVision = 4;
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

    public ArrayList<Player> getAvailableStealTarget(){
        ArrayList<Player> playerList =  new ArrayList<>();
        Player nextPlayer = this.nextPlayer;
        Player prevPlayer = this.prevPlayer;
        int distance=1;

        while(distance <= vision){
            if((nextPlayer.evasion+distance)<=vision){
                playerList.add(nextPlayer);
            }
            if((prevPlayer.evasion+distance)<=vision && prevPlayer != nextPlayer){
                playerList.add(prevPlayer);
            }
            if(nextPlayer == prevPlayer || (nextPlayer.nextPlayer == prevPlayer && prevPlayer.prevPlayer == nextPlayer)){
                break;
            }
            nextPlayer = nextPlayer.nextPlayer;
            prevPlayer = prevPlayer.prevPlayer;
            distance++;
        }
        return playerList;
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

    public boolean canJail(){
        if(this.role == Role.SHERIF) return true;
        else{
            Player player = this.nextPlayer;
            while(player.id != this.id){
                if(player.role != Role.SHERIF) return true;
                player = this.nextPlayer;
            }
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
