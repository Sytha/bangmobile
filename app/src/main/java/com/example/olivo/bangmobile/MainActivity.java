package com.example.olivo.bangmobile;
import com.example.olivo.bangmobile.consoleInterfaceSolo.ConsoleInterface;
import com.example.olivo.bangmobile.gameMechanics.*;
import com.example.olivo.bangmobile.gameMechanics.elements.Card;
import com.example.olivo.bangmobile.gameMechanics.elements.Figure;
import com.example.olivo.bangmobile.gameMechanics.elements.Player;
import com.example.olivo.bangmobile.gameMechanics.elements.Role;

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

        setContentView(R.layout.activity_main);
        ScrollView display = (ScrollView) findViewById(R.id.scrollView);
        TextView text = new TextView(this);
        String textToDisplay = "Players infos : \n";



/*        ConsoleInterface ci = new ConsoleInterface(getApplicationContext());
        textToDisplay += ci.getPlayersInfo();*/








        text.setText(textToDisplay);
        display.addView(text);



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
