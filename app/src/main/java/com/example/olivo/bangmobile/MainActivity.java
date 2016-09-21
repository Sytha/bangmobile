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
        String gameInfoString = gameInfo.getText() + textToDisplay;
        gameInfo.setText(gameInfoString);
        LinearLayout chkList = (LinearLayout) findViewById(R.id.checkboxList);
        chkList.removeAllViews();
        ci.getActionList();
    }

    public void displaySimpleActionButton(String buttonText, final int actionId){
        LinearLayout checkboxList = (LinearLayout) findViewById(R.id.checkboxList);
        Button myButton = new Button(this);
        myButton.setText(buttonText);
        myButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                TextView gameinfo = (TextView) findViewById(R.id.gameInfo);
                gameinfo.setText("");
                ci.selectMove(actionId);
            }
        });
        checkboxList.addView(myButton);
    }

    public void displayCheckBoxPickCardMove(String title, HashMap<Integer, String> cardList, final int maxAmountChecked){
        final LinearLayout checkBoxList = (LinearLayout) findViewById(R.id.checkboxList);
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
        final LinearLayout checkBoxList = (LinearLayout) findViewById(R.id.checkboxList);
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
        final LinearLayout checkBoxList = (LinearLayout) findViewById(R.id.checkboxList);
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
        LinearLayout checkboxList = (LinearLayout) findViewById(R.id.checkboxList);
        checkboxList.removeAllViews();
    }

    public void displayButtonTargetMove(String title, HashMap<Integer, String> stringPlayerList) {
        final LinearLayout checkBoxList = (LinearLayout) findViewById(R.id.checkboxList);
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
}
