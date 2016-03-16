package com.example.olivo.bangmobile.gameMechanics;

import android.content.Context;

import com.example.olivo.bangmobile.gameMechanics.elements.Card;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.Action;
import com.example.olivo.bangmobile.gameMechanics.elements.Figure;
import com.example.olivo.bangmobile.gameMechanics.elements.Player;
import com.example.olivo.bangmobile.gameMechanics.elements.Role;
import com.example.olivo.bangmobile.gameMechanics.elements.Turn;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by olivo on 07/01/2016.
 */
public class Game {
    Map<Integer,Player> players;
    Player currentPlayer;
    Context context;
    Turn currentTurn;
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

    public void startGame(String name){
        this.setFigures();
        currentPlayer = this.setRoles(); //Set Role and start with Sherif
        currentTurn = new Turn(currentPlayer,players);
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
            switch(figure.id){
                case PAUL_REGRET:
                    players.get(playerNumber).evasion+=1;
                    break;
                case ROSE_DOOLAN:
                    players.get(playerNumber).vision+=2;
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



}

