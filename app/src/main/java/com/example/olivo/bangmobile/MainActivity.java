package com.example.olivo.bangmobile;
import com.example.olivo.bangmobile.consoleInterfaceSolo.ConsoleInterface;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    public ConsoleInterface ci;
    private int pickCardChecked;
    private ArrayList<Integer> pickCardCheckedList;

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

    public void displayPickCardMove(String title, HashMap<Integer,String> cardList, final int maxAmountChecked){
        LinearLayout buttonList = (LinearLayout) findViewById(R.id.checkboxList);
        this.pickCardChecked=0;
        TextView titleTV = new TextView(this);
        titleTV.setText(title);
        buttonList.addView(titleTV);
        for(Map.Entry<Integer,String> entry : cardList.entrySet()){
            final CheckBox chk = new CheckBox(this);
            chk.setText(entry.getValue());
            chk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked ){
                        if(pickCardChecked == maxAmountChecked){
                            chk.setChecked(false);
                            displayPickCardError("Nb carte max = " + maxAmountChecked);
                        }else{
                            pickCardChecked++;
                        }
                    }else{
                        pickCardChecked--;
                    }
                }
            });
            buttonList.addView(chk);
        }
    }

    private void displayPickCardError(String error) {
        LinearLayout buttonList = (LinearLayout) findViewById(R.id.checkboxList);
        TextView errorTV = new TextView(this);
        errorTV.setText(error);
        buttonList.addView(errorTV);
    }}
