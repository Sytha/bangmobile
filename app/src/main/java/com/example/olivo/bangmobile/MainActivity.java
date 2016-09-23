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
    private String gameInfoString = "";
    private boolean allGameInfoDisplayed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ci = new ConsoleInterface(getApplicationContext(),this);
        displayInfoAndControl();
    }

    public void displayInfoAndControl(){
        TextView playerInfo = (TextView) findViewById(R.id.playerInfo);

        String textToDisplay = ci.getPlayersInfo();
        playerInfo.setText(textToDisplay);
        textToDisplay = ci.getGameInfo();
        this.gameInfoString += textToDisplay;
        this.displayGameInfo();
        LinearLayout chkList = (LinearLayout) findViewById(R.id.control);
        chkList.removeAllViews();
        ci.getActionList();
    }

    public void displaySimpleActionButton(String title, String buttonText, final int actionId){
        LinearLayout control = (LinearLayout) findViewById(R.id.control);

        TextView tv = (TextView) findViewById(R.id.controlTitle);
        tv.setText(title);
        Button myButton = new Button(this);
        myButton.setText(buttonText);
        myButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ci.selectMove(actionId);
            }
        });
        control.addView(myButton);
    }

    public void displayCheckBoxPickCardMove(String title, HashMap<Integer, String> cardList, final int maxAmountChecked){
        final LinearLayout checkBoxList = (LinearLayout) findViewById(R.id.control);
        checkBoxList.removeAllViews();
        this.pickCardChecked=0;
        TextView titleTV = new TextView(this);
        titleTV.setText(title);
        checkBoxList.addView(titleTV);
        for(final Map.Entry<Integer,String> entry : cardList.entrySet()){
            final CheckBox chk = new CheckBox(this);
            chk.setText(entry.getValue());
            chk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked ){
                        if(pickCardChecked == maxAmountChecked){
                            chk.setChecked(false);
                            displayErrorMessage("Sélectionner " + maxAmountChecked + " cartes");
                        }else{
                            pickCardChecked++;
                            ci.pickCard(entry.getKey(),isChecked);
                            displayErrorMessage("");
                        }
                    }else{
                        pickCardChecked--;
                        ci.pickCard(entry.getKey(),isChecked);
                        displayErrorMessage("");
                    }
                }
            });
            checkBoxList.addView(chk);
        }
        Button validateButton = new Button(this);
        validateButton.setText("Valider");
        validateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (pickCardChecked == maxAmountChecked) {
                    displayErrorMessage("");
                    checkBoxList.removeAllViews();
                    ci.selectPickCardMove();
                } else {
                    displayErrorMessage("Sélectionner " + maxAmountChecked + " cartes");
                }
            }
        });
        checkBoxList.addView(validateButton);
        validateButton = new Button(this);
        validateButton.setText("Retour");
        validateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ci.getActionList();
            }
        });
        checkBoxList.addView(validateButton);
    }

    public void displayButtonChoiceMove(String title, HashMap<Integer, String> answerList){
        final LinearLayout checkBoxList = (LinearLayout) findViewById(R.id.control);
        checkBoxList.removeAllViews();
        this.pickCardChecked=0;
        TextView titleTV = new TextView(this);
        titleTV.setText(title);
        checkBoxList.addView(titleTV);
        for(final Map.Entry<Integer,String> entry : answerList.entrySet()){
            final Button button = new Button(this);
            button.setText(entry.getValue());
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    ci.selectAnswer(entry.getKey());
                }
            });
            checkBoxList.addView(button);
        }
    }

    public void displayButtonPlayCardMove(String title, HashMap<Integer, String> answerList){
        final LinearLayout checkBoxList = (LinearLayout) findViewById(R.id.control);
        checkBoxList.removeAllViews();
        this.pickCardChecked=0;
        TextView titleTV = new TextView(this);
        titleTV.setText(title);
        checkBoxList.addView(titleTV);
        for(final Map.Entry<Integer,String> entry : answerList.entrySet()){
            final Button button = new Button(this);
            button.setText(entry.getValue());
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    ci.selectCard(entry.getKey());
                }
            });
            checkBoxList.addView(button);
        }
        Button validateButton = new Button(this);
        validateButton.setText("Retour");
        validateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ci.getActionList();
            }
        });
        checkBoxList.addView(validateButton);
    }

    private void displayErrorMessage(String error) {
        TextView errorTV = (TextView) findViewById(R.id.errorMessage);
        errorTV.setText(error);
    }

    public void clearButtonList() {
        LinearLayout checkboxList = (LinearLayout) findViewById(R.id.control);
        checkboxList.removeAllViews();
    }

    public void displayButtonTargetMove(String title, HashMap<Integer, String> stringPlayerList) {
        final LinearLayout checkBoxList = (LinearLayout) findViewById(R.id.control);
        checkBoxList.removeAllViews();
        TextView titleTV = new TextView(this);
        titleTV.setText(title);
        checkBoxList.addView(titleTV);
        for(final Map.Entry<Integer,String> entry : stringPlayerList.entrySet()){
            final Button button = new Button(this);
            button.setText(entry.getValue());
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    ci.selectPlayer(entry.getKey());
                }
            });
            checkBoxList.addView(button);
        }
    }

    private void displayGameInfo(){
        TextView gameInfo = (TextView) findViewById(R.id.gameInfo);
        Button toggle = (Button) findViewById(R.id.toggleGameInfo);
        String gameInfoToDisplay = "";
        String buttonString = "";
        if(allGameInfoDisplayed){
            gameInfoToDisplay = gameInfoString;
            buttonString = "Show Less";
        }else{
            String[] gameInfoLines = gameInfoString.split("\n");
            int idStart = gameInfoLines.length - 5;
            if(idStart < 0){
                idStart = 0;
            }
            for(int i = idStart ; i < gameInfoLines.length ; i++){
                gameInfoToDisplay += gameInfoLines[i] + "\n";
            }
            buttonString = "Show More";
        }

        gameInfo.setText(gameInfoToDisplay);
        toggle.setText(buttonString);
        toggle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                allGameInfoDisplayed = !allGameInfoDisplayed;
                displayGameInfo();
            }
        });


    }
}
