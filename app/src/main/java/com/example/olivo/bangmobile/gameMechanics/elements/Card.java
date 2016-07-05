package com.example.olivo.bangmobile.gameMechanics.elements;

import android.content.Context;
import android.content.res.XmlResourceParser;

import com.example.olivo.bangmobile.R;

import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;

/**
 * Created by olivo on 05/01/2016.
 */
public class Card implements Cloneable {
    public static int ids = 0;
    public int uniqueID;
    public Card_id id;
    public String description;
    public Card_type type; //weapon,action,ability;



    public static ArrayList<Card> availableCards=null;

    public enum Card_type {
        WEAPON,
        ACTION,
        ABILITY
    }

    public enum Card_id {
        //ACTION
        BANG,
        BIERE,
        RATE,
        SALOON,
        MAGASIN,
        INDIENS,
        GATLING,
        CONVOI,
        DILIGENCE,
        BRAQUAGE,
        COUPDEFOUDRE,
        DUEL,
        PRISON,
        //ABILITY
        MUSTANG,
        LUNETTE,
        DYNAMITE,
        PLANQUE,
        //WEAPON
        VOLCANIQUE,
        SCHOFIELD,
        REMINGTON,
        CARABINE,
        WINCHESTER



    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    static public void populateCard(Context context){
        XmlResourceParser xrp = context.getResources().getXml(R.xml.card);
        availableCards = new ArrayList<>();
        Card card = null;
        try{
            xrp.next();
            int eventType = xrp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && xrp.getName().equalsIgnoreCase("card")) {
                    card = new Card();
                }else if(eventType == XmlPullParser.START_TAG && xrp.getName().equalsIgnoreCase("id")){
                    eventType = xrp.next();
                    int new_id=Integer.parseInt(xrp.getText());
                    switch(new_id){
                        case 1:
                            card.id= Card_id.BANG;
                            break;
                        case 2:
                            card.id= Card_id.BIERE;
                            break;
                        case 3:
                            card.id= Card_id.MUSTANG;
                            break;
                        case 4:
                            card.id= Card_id.LUNETTE;
                            break;
                        case 5:
                            card.id= Card_id.PLANQUE;
                            break;
                        case 6:
                            card.id= Card_id.RATE;
                            break;
                        case 7:
                            card.id= Card_id.SALOON;
                            break;
                        case 8:
                            card.id= Card_id.MAGASIN;
                            break;
                        case 9:
                            card.id= Card_id.INDIENS;
                            break;
                        case 10:
                            card.id= Card_id.GATLING;
                            break;
                        case 11:
                            card.id= Card_id.DYNAMITE;
                            break;
                        case 12:
                            card.id= Card_id.CONVOI;
                            break;
                        case 13:
                            card.id= Card_id.DILIGENCE;
                            break;
                        case 14:
                            card.id= Card_id.BRAQUAGE;
                            break;
                        case 15:
                            card.id= Card_id.COUPDEFOUDRE;
                            break;
                        case 16:
                            card.id= Card_id.DUEL;
                            break;
                        case 17:
                            card.id= Card_id.PRISON;
                            break;
                        case 18:
                            card.id= Card_id.VOLCANIQUE;
                            break;
                        case 19:
                            card.id= Card_id.SCHOFIELD;
                            break;
                        case 20:
                            card.id= Card_id.REMINGTON;
                            break;
                        case 21:
                            card.id= Card_id.CARABINE;
                            break;
                        case 22:
                            card.id= Card_id.WINCHESTER;
                            break;
                    }
                }else if(eventType == XmlPullParser.START_TAG && xrp.getName().equalsIgnoreCase("desc")){
                    eventType = xrp.next();
                    card.description=xrp.getText();
                }else if(eventType == XmlPullParser.START_TAG && xrp.getName().equalsIgnoreCase("type")){
                    eventType = xrp.next();
                    int typeInt=Integer.parseInt(xrp.getText());
                    switch (typeInt){
                        case 1:
                            card.type=Card_type.WEAPON;
                            break;
                        case 2:
                            card.type=Card_type.ACTION;
                            break;
                        case 3:
                            card.type=Card_type.ABILITY;
                            break;
                    }
                }else if(eventType == XmlPullParser.END_TAG && xrp.getName().equalsIgnoreCase("card")){
                    availableCards.add(card);
                }
                eventType = xrp.next();
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    static public Card getCardFromCard_id(Card_id id){
        Card newCard = new Card();
        for(Card card : availableCards){
            if(card.id==id){
                newCard.description = card.description;
                newCard.type = card.type;
                newCard.id = card.id;
                newCard.uniqueID = ids++;
                break;
            }
        }
        return newCard;
    }


    static public ArrayList<Card> getAvailableCards(Context context){
        if(availableCards == null) {
            Card.populateCard(context);
        }
        return availableCards;
    }
}
