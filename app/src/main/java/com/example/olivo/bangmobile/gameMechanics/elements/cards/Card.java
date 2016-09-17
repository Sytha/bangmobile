package com.example.olivo.bangmobile.gameMechanics.elements.cards;

import android.content.Context;
import android.content.res.XmlResourceParser;

import com.example.olivo.bangmobile.R;
import com.example.olivo.bangmobile.gameMechanics.Game;
import com.example.olivo.bangmobile.gameMechanics.elements.Player;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.Move;

import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;


public abstract class Card {
    public static int ids = 0;
    public int uniqueID;
    public Card_id id;
    public String description;
    public Card_type type; //weapon,action,ability;
    public CardColor cardColor;
    public int cardValue;
    public static ArrayList<Card> availableCards=null;
    public boolean actionEnded = false;

    public enum CardColor{HEART,DIAMOND,PIKE,CLUB}

    public enum Card_type {
        WEAPON,
        ACTION,
        ABILITY
    }

    public enum Card_id {
        //ACTION
        BANG,
        BEER,
        MISS,
        SALOON,
        SHOP,
        APACHE,
        GATLING,
        CONVOY,
        DILIGENCE,
        ROBBERY,
        LOVESTRIKE,
        DUEL,
        JAIL,
        //ABILITY
        MUSTANG,
        SCOPE,
        DYNAMITE,
        HIDEOUT,
        //WEAPON
        VOLCANIC,
        SCHOFIELD,
        REMINGTON,
        CARBINE,
        WINCHESTER
    }


    /*
    CARD FUNCTIONS
     */

    public boolean usable(Player player, Game game){
        //A SURCHAGER SI BESOIN
        return true;
    }

    abstract public void play(Player source, Game game);

    public void action(Player source, Move move, Game game){
        //A SURCHARGER SI BESOIN
    }

    public void addBoardCardEffect(Player player){
        //A SURCHARGER SI BESOIN
    }

    public void removeBoardCardEffect(Player player){
        //A SURCHARGER SI BESOIN
    }

    public void reset(){
        actionEnded = false;
    }

    /*
    STATIC CLASSES
     */

    static public void populateCard(Context context){
        XmlResourceParser xrp = context.getResources().getXml(R.xml.card);
        availableCards = new ArrayList<>();
        Card card = null ;
        try{
            xrp.next();
            int eventType = xrp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if(eventType == XmlPullParser.START_TAG && xrp.getName().equalsIgnoreCase("id")){
                    xrp.next();
                    int new_id=Integer.parseInt(xrp.getText());
                    switch(new_id){
                        case 1:
                            card = new CardBang();
                            card.id= Card_id.BANG;
                            break;
                        case 2:
                            card = new CardBeer();
                            card.id= Card_id.BEER;
                            break;
                        case 3:
                            card = new CardMustang();
                            card.id= Card_id.MUSTANG;
                            break;
                        case 4:
                            card = new CardScope();
                            card.id= Card_id.SCOPE;
                            break;
                        case 5:
                            card = new CardHideOut();
                            card.id= Card_id.HIDEOUT;
                            break;
                        case 6:
                            card = new CardMiss();
                            card.id= Card_id.MISS;
                            break;
                        case 7:
                            card = new CardSaloon();
                            card.id= Card_id.SALOON;
                            break;
                        case 8:
                            card = new CardShop();
                            card.id= Card_id.SHOP;
                            break;
                        case 9:
                            card = new CardApache();
                            card.id= Card_id.APACHE;
                            break;
                        case 10:
                            card = new CardGatling();
                            card.id= Card_id.GATLING;
                            break;
                        case 11:
                            card = new CardDynamite();
                            card.id= Card_id.DYNAMITE;
                            break;
                        case 12:
                            card = new CardConvoy();
                            card.id= Card_id.CONVOY;
                            break;
                        case 13:
                            card = new CardDiligence();
                            card.id= Card_id.DILIGENCE;
                            break;
                        case 14:
                            card = new CardRobbery();
                            card.id= Card_id.ROBBERY;
                            break;
                        case 15:
                            card = new CardLoveStrike();
                            card.id= Card_id.LOVESTRIKE;
                            break;
                        case 16:
                            card = new CardDuel();
                            card.id= Card_id.DUEL;
                            break;
                        case 17:
                            card = new CardJail();
                            card.id= Card_id.JAIL;
                            break;
                        case 18:
                            card = new CardVolcanic();
                            card.id= Card_id.VOLCANIC;
                            break;
                        case 19:
                            card = new CardSchofield();
                            card.id= Card_id.SCHOFIELD;
                            break;
                        case 20:
                            card = new CardRemington();
                            card.id= Card_id.REMINGTON;
                            break;
                        case 21:
                            card = new CardCarbine();
                            card.id= Card_id.CARBINE;
                            break;
                        case 22:
                            card = new CardWinchester();
                            card.id= Card_id.WINCHESTER;
                            break;
                    }
                }else if(eventType == XmlPullParser.START_TAG && xrp.getName().equalsIgnoreCase("desc")&& card != null){
                    xrp.next();
                    card.description=xrp.getText();
                }else if(eventType == XmlPullParser.START_TAG && xrp.getName().equalsIgnoreCase("type")&& card != null){
                    xrp.next();
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
                }else if(eventType == XmlPullParser.START_TAG && xrp.getName().equalsIgnoreCase("values")&& card != null){
                    xrp.next();
                    String[] values =xrp.getText().split(";");
                    for(String value : values){
                        String[] cardValues = value.split("-");
                        switch (cardValues[1]) {
                            case "H":
                                card.cardColor = CardColor.HEART;
                                break;
                            case "P":
                                card.cardColor = CardColor.PIKE;
                                break;
                            case "C":
                                card.cardColor = CardColor.CLUB;
                                break;
                            case "D":
                                card.cardColor = CardColor.DIAMOND;
                                break;
                        }
                        card.cardValue = Integer.parseInt(cardValues[0]);
                        card.uniqueID = ids++;
                        availableCards.add((Card)card.clone());
                    }
                }
                eventType = xrp.next();
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    static public ArrayList<Card> getAvailableCards(Context context){
        if(availableCards == null) {
            Card.populateCard(context);
        }
        @SuppressWarnings(value = "unchecked")
        ArrayList<Card> cardsList = (ArrayList<Card>) availableCards.clone();

        return cardsList;
    }
}
