package com.example.olivo.bangmobile.consoleInterfaceSolo;

import android.content.Context;

import com.example.olivo.bangmobile.gameMechanics.Game;
import com.example.olivo.bangmobile.gameMechanics.elements.Card;
import com.example.olivo.bangmobile.gameMechanics.elements.Figure;
import com.example.olivo.bangmobile.gameMechanics.elements.Player;
import com.example.olivo.bangmobile.gameMechanics.elements.Role;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by olivo on 10/08/2016.
 */
public class ConsoleInterface {
    Game game;
    Context context;
    public ConsoleInterface(Context context){
        ArrayList<Player> players = new ArrayList<>();
        players.add(new Player(0, "p1"));
        players.add(new Player(1, "p2"));
        players.add(new Player(2, "p3"));
        players.add(new Player(3, "p4"));
        this.context=context;
        game = new Game(context, players );
        game.startGame();
    }



    public String getSecretPlayersInfo(){
        String info = "";
        for(Map.Entry e : game.players.entrySet()){
            Player p =  (Player) e.getValue();
            info += "\npos :" + e.getKey() + " info : " + p.id + ", " + p.figure.id + ", "+ p.role + ", " + p.healthPoint + "/" + p.maxHealthPoint ;
        }

        return info;
    }

    public String getPlayersInfo(){
        String info = "";
        for(Map.Entry e : game.players.entrySet()){
            Player p =  (Player) e.getValue();
            info += "\n" + p.name + "("+p.figure.id +")";
            if(p.role == Role.SHERIF){
                info += " (SHERIF)";
            }
            info += "\nHP :" + p.healthPoint + "/" + p.maxHealthPoint;
            info += " Vision :" + p.vision;
            info += " WVision :" + p.weaponVision;
            info += " Evasion :" + p.evasion;
            info += "\nHand : " + p.handCards.size() + "(";
            String add ="";
            for(Card card : p.handCards){
                info += add+card.id+"("+card.cardValue+"-"+card.cardColor+")";
                add=", ";
            }
            info += ")\n";
        }

        return info;
    }

    public String getCardsList(){
        String info="";
        for(Card card : game.cardDeque){
            info+="\n"+card.id;
        }
        return info;
    }

}
