package com.example.olivo.bangmobile;
import com.example.olivo.bangmobile.consoleInterfaceSolo.ConsoleInterface;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ScrollView display = (ScrollView) findViewById(R.id.scrollView);
        TextView text = new TextView(this);
        String textToDisplay = "\n\n\n\nPlayers infos : \n";



        ConsoleInterface ci = new ConsoleInterface(getApplicationContext());
        textToDisplay += ci.getPlayersInfo();

        textToDisplay += "\n" + ci.getCardsList();








        text.setText(textToDisplay);
        display.addView(text);



        Button button = (Button)this.findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                reset();
            }
        });

    }

    public void reset(){
        setContentView(R.layout.activity_main);
        ScrollView display = (ScrollView) findViewById(R.id.scrollView);
        TextView text = new TextView(this);
        String textToDisplay = "\n" +
                "\n" +
                "\n" +
                "\nPlayers infos : \n";



        ConsoleInterface ci = new ConsoleInterface(getApplicationContext());
        textToDisplay += ci.getPlayersInfo();

        textToDisplay += "\n" + ci.getCardsList();








        text.setText(textToDisplay);
        display.addView(text);
        Button button = (Button)this.findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                reset();
            }
        });
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
