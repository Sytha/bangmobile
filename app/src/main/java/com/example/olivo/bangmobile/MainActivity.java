package com.example.olivo.bangmobile;
import com.example.olivo.bangmobile.gameMechanics.*;
import com.example.olivo.bangmobile.gameMechanics.elements.Card;
import com.example.olivo.bangmobile.gameMechanics.elements.Figure;
import com.example.olivo.bangmobile.gameMechanics.elements.Player;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

/*
        setContentView(R.layout.activity_main);
        ScrollView display = (ScrollView) findViewById(R.id.scrollView);
        TextView text = new TextView(this);
        String textToDisplay = "";

        textToDisplay += "Figures /////////////////////////\n\n\n";
        for(Figure f : Figure.getAvailableFigures(getApplicationContext())){
            textToDisplay += f.name + "\nhp : " + f.baseHealthPoint + "\nDescription : " + f.description + "\n\n\n";
        }
        textToDisplay += "Cards /////////////////////////\n\n\n";
        for(Card c : Card.getAvailableCards(getApplicationContext())){
            textToDisplay += c.name + "\ntype : " + c.type + "\nDescription : " + c.description + "\n\n\n";
        }
        textToDisplay += "Gametypes /////////////////////////\n\n\n";
        for(Map.Entry<Integer, ArrayList<Integer>> entry: GameType.getRolesByGameTypes(getApplicationContext()).entrySet()){
            textToDisplay += "Pour "+entry.getKey()+" joueurs : \n";
            for(int s : entry.getValue()){
                textToDisplay += "-"+showRole(s)+"\n";
            }
        }


        ArrayList<Player> players = new ArrayList<>();
        Player p1 = new Player();
        p1.name = "p1";
        players.add(p1);
        Player p2 = new Player();
        p2.name = "p2";
        players.add(p2);
        Player p3 = new Player();
        p3.name = "p3";
        players.add(p3);
        ArrayList<Integer> roles = new ArrayList<>();
        roles.add(1);
        roles.add(2);
        roles.add(3);
        int player=0;
        Random random = new Random();
        while(roles.size()!=0){
            players.get(player++).role = roles.remove(random.nextInt(roles.size()));
        }

        textToDisplay += "Test Players & Roles /////////////////////////\n\n\n";

        textToDisplay += "P1 " + showRole(p1.role) + "\n";
        textToDisplay += "P2 " + showRole(p2.role) + "\n";
        textToDisplay += "P3 " + showRole(p3.role) + "\n";





        text.setText(textToDisplay);
        display.addView(text);


*/

    }

    public static String showRole(int role){
        switch(role){
            case 1: return "Sherif";
            case 2: return "Outlaw";
            case 3: return "Renegat";
            case 4: return "Deputy";
        }
        return "lol";
    }

}
