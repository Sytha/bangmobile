package com.example.olivo.bangmobile;
import com.example.olivo.bangmobile.consoleInterfaceSolo.ConsoleInterface;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    public ConsoleInterface ci;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ci = new ConsoleInterface(getApplicationContext(),this);
        displayInfoAndControl();
    }

    public void displayInfoAndControl(){
        TextView playerInfo = (TextView) findViewById(R.id.playerInfo);
        TextView gameInfo = (TextView) findViewById(R.id.gameInfo);

        String textToDisplay = ci.getPlayersInfo();
        playerInfo.setText(textToDisplay);
        textToDisplay = ci.getGameInfo();
        gameInfo.setText(textToDisplay);
        LinearLayout buttonList = (LinearLayout) findViewById(R.id.buttonList);
        buttonList.removeAllViews();
        ci.getActionList();
    }

    public void displaySimpleActionButton(String buttonText, final int actionId){
        LinearLayout buttonList = (LinearLayout) findViewById(R.id.buttonList);
        Button myButton = new Button(this);
        myButton.setText(buttonText);
        myButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ci.selectSimpleMove(actionId);
            }
        });
        buttonList.addView(myButton);
    }
}
