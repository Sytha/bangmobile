package com.example.olivo.bangmobile.gameMechanics;

import android.content.Context;

import com.example.olivo.bangmobile.gameMechanics.elements.cards.Card;
import com.example.olivo.bangmobile.gameMechanics.interactions.Interaction;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.Action;
import com.example.olivo.bangmobile.gameMechanics.elements.Figure;
import com.example.olivo.bangmobile.gameMechanics.elements.Player;
import com.example.olivo.bangmobile.gameMechanics.elements.Role;
import com.example.olivo.bangmobile.gameMechanics.elements.Turn;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.ChoiceMove;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.GetCardMove;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.Move;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.PassMove;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.PickCardMove;
import com.example.olivo.bangmobile.gameMechanics.interactions.infos.Info;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by olivo on 07/01/2016.
 */
public class Game {
    public Map<Integer,Player> players;
    public Player currentPlayer;
    Context context;
    public Turn currentTurn;
    public Deque<Interaction> interactionStack;
    public Deque<Card> cardDeque;
    public Deque<Card> throwDeque;

    public Game(Context context, ArrayList<Player> playersList){
        this.context=context;
        players = new HashMap<>();
        setPlayersPosition(playersList);
        currentPlayer=null;
    }

/////////////////////////////////////////////////
// START GAME FUNCTIONS
////////////////////////////////////////////////

    public void setPlayersPosition(ArrayList<Player> newPlayers){
        Random rand = new Random();
        int playerAmount = 0;
        Player prevPlayer = null;
        Player firstPlayer = null;
        while(!newPlayers.isEmpty()){
            Player newPlayer = newPlayers.remove(rand.nextInt(newPlayers.size()));
            if(prevPlayer != null){
                newPlayer.prevPlayer=prevPlayer;
                prevPlayer.nextPlayer=newPlayer;
            }
            if(firstPlayer == null){
                firstPlayer=newPlayer;
            }
            prevPlayer=newPlayer;
            players.put(playerAmount++, newPlayer);
        }
        firstPlayer.prevPlayer=prevPlayer;
        prevPlayer.nextPlayer=firstPlayer;
    }

    public void startGame(){
        this.setFigures();
        currentPlayer = this.setRoles(); //Set Role and start with Sherif
        this.createDeque();
        this.addFirstCard();
        //currentTurn = new Turn(currentPlayer,players);
    }

    public void addFirstCard() {
        boolean finish=false;
        int nbTurn=0;
        Player player;
        while(!finish){
            player = currentPlayer;
            finish=true;
            do{
                if(nbTurn < player.maxHealthPoint){
                    player.handCards.add(cardDeque.pop());
                    finish=false;
                }
                player=player.nextPlayer;
            }while(player != currentPlayer);
            nbTurn++;
        }


    }

    /**
        set Players Roles and return Sherif Player
     */
    public Player setRoles(){
        ArrayList<Role> roles = GameType.getGameTypeRoles(context, players.size());
        int playerNumber=0;
        Random random = new Random();
        Player sherif=null;
        while(roles.size()!=0){
            Role role = roles.remove(random.nextInt(roles.size()));
            players.get(playerNumber).role = role;
            if(role == Role.SHERIF){
                sherif=players.get(playerNumber);
                players.get(playerNumber).healthPoint+=1;
                players.get(playerNumber).maxHealthPoint+=1;
            }
            playerNumber++;
        }
        return sherif;
    }

    /**
     set Players Figure
     */
    public void setFigures(){
        ArrayList<Figure> figures = (ArrayList<Figure>) Figure.getAvailableFigures(context).clone();
        int playerNumber=0;
        Random random = new Random();
        while(playerNumber < players.size()){
            Figure figure = figures.remove(random.nextInt(figures.size()));
            players.get(playerNumber).figure = figure;
            players.get(playerNumber).healthPoint += figure.baseHealthPoint;
            players.get(playerNumber).maxHealthPoint += figure.baseHealthPoint;
            switch(figure.id){
                case PAUL_REGRET:
                    players.get(playerNumber).evasion+=1;
                    break;
                case ROSE_DOOLAN:
                    players.get(playerNumber).vision+=1;
                    break;
            }
            playerNumber++;
        }
    }

    public Player getNextPlayer(){
        Player player= null;
        if(currentPlayer != null){
            if(currentPlayer.id+1 == players.size()){
                player=players.get(0);
            }else{
                player=players.get(currentPlayer.id+1);
            }
        }
        return player;
    }

    public void createDeque(){
        ArrayList<Card> baseCardList = Card.getAvailableCards(context);

        cardDeque = new ArrayDeque<>();
        Random random = new Random();

        while(baseCardList.size()>0){
            cardDeque.add(baseCardList.remove(random.nextInt(baseCardList.size())));
        }
    }



/*
    public void firstTurn(){
        Player currentPlayer = null;
        for(Player p : players){
            if(p.role== Role.SHERIF){
                currentPlayer = p;
                break;
            }
        }
    }
*/

    public void nextTurn(){
        currentPlayer = players.get(currentPlayer.id);
    }

    public Action getNextAction(){
        return null;
    }

/*
    public void addPlayer(String name){
        Player newPlayer = new Player();
        newPlayer.name = name;
        newPlayer.id = idPlayer;
        players.add(idPlayer++,newPlayer);
    }

    public void removePlayer(String name){
        for(Player p : players){
            if(p.name.equals(name)){
                players.remove(p);
                break;
            }
        }
    }
*/

    ////////////////////////////////////////////
    //GAME FUNCTIONS
    ////////////////////////////////////////////

    public boolean quickDraw(ArrayList<Card.CardColor> cardColors, int cardValueMin, int cardValueMax){
        Card card = cardDeque.pop();
        throwDeque.push(card);
        if(cardColors.contains(card.cardColor) && card.cardValue >= cardValueMin && card.cardValue <= cardValueMax){
            return true;

        }
        return false;
    }

    public void quickDrawLuckyDuck(ArrayList<Card.CardColor> cardColors, int cardValueMin, int cardValueMax){
        ArrayList<Card> cardsToGet = new ArrayList<>();
        cardsToGet.add(cardDeque.pop());
        cardsToGet.add(cardDeque.pop());
        ArrayList<Move> moveList = new ArrayList<>();
        moveList.add(new PickCardMove(cardsToGet,1, PickCardMove.PickType.LUCKYDUKEDRAW));
        interactionStack.addLast(new Info(currentPlayer, Info.InfoType.LUCKYDUKEDRAW));
        interactionStack.addLast(new Action(currentPlayer, moveList));
    }


    public boolean checkMort(Player player) {
        if(player.healthPoint <= 0){
            interactionStack.addLast(new Info(player, Info.InfoType.DYING));
            ArrayList<Move> movesList = new ArrayList<>();
            if(players.values().size()>2){
                if(player.hasAmountOfCardInHand(Card.Card_id.BIERE,(player.healthPoint*-1+1))){
                    movesList.add(new ChoiceMove(ChoiceMove.Choice.SAVEBEER));
                }
            }
            if(player.handCards.size()>=(player.healthPoint*-1+1)*2 && player.figure.id == Figure.fig_id.SID_KETCHUM){
                movesList.add(new PickCardMove(player.handCards,(player.healthPoint*-1+1)*2, PickCardMove.PickType.HEALTHROW));
            }
            movesList.add(new PassMove(PassMove.PassReason.ENDLIFE));
            interactionStack.addLast(new Action(player,movesList));
            return true;
        }
        return false;
    }

    public void drawBartCassidy(Player victim, int damageDealt){
        if(victim.figure.id == Figure.fig_id.BART_CASSIDY){
            ArrayList<Move> moveList = new ArrayList<>();
            ArrayList<Card> cards = new ArrayList<>();
            for(int i=0; i<damageDealt; i++){
                cards.add(cardDeque.pop());
            }
            moveList.add(new GetCardMove(cards));
            interactionStack.addLast(new Action(victim,moveList));
            Info info =  new Info(victim, Info.InfoType.BARTCASSIDYDRAW);
            interactionStack.addLast(info);
        }
    }

}

