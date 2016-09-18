package com.example.olivo.bangmobile;
import com.example.olivo.bangmobile.consoleInterfaceSolo.ConsoleInterface;
import com.example.olivo.bangmobile.gameMechanics.interactions.Interaction;
import com.example.olivo.bangmobile.gameMechanics.interactions.infos.Info;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    public ConsoleInterface ci;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ci = new ConsoleInterface(getApplicationContext());

        displayInfoAndControl();

    }

    public void displayInfoAndControl(){
        setContentView(R.layout.activity_main);
        ScrollView playerInfo = (ScrollView) findViewById(R.id.playerInfo);
        ScrollView gameInfo = (ScrollView) findViewById(R.id.gameInfo);
        TextView playerInfoText = new TextView(this);
        TextView gameInfoText = new TextView(this);

        ////////////////
        //Player Info
        ////////////////
        String textToDisplay = "\n\n\n\nPlayers infos : \n";
        textToDisplay += ci.getPlayersInfo();
        textToDisplay += "\n" + ci.getCardsList();
        playerInfoText.setText(textToDisplay);
        playerInfo.addView(playerInfoText);

        ////////////////
        //Game Info
        ////////////////
        textToDisplay = "\n\nGame infos : \n";
        boolean isAction = false;
        Interaction interaction;

        while(!isAction){
            interaction = ci.getNextInteraction();
            if(interaction.type == Interaction.Types.INFO){
                Info info = (Info) interaction;
                textToDisplay += "\n" + info.player + " - " + info.info;
            }
        }
        gameInfoText.setText(textToDisplay);
        gameInfo.addView(gameInfoText);

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
