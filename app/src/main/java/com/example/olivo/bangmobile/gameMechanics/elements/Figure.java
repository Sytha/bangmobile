package com.example.olivo.bangmobile.gameMechanics.elements;

import android.content.Context;
import android.content.res.XmlResourceParser;

import com.example.olivo.bangmobile.R;

import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;

/**
 * Created by olivo on 05/01/2016.
 */
public class Figure {
    public fig_id id;
    public String name;
    public String description;
    public int baseHealthPoint;
    public static ArrayList<Figure> availableFigures=null;

    public enum fig_id {
        SUZY_LAFAYETTE,
        PAUL_REGRET ,
        PEDRO_RAMIREZ ,
        CALAMITY_JANET,
        EL_GRINGO,
        JOURDONNAIS,
        LUCKY_DUKE,
        SLAB_THE_KILLER,
        WILLY_THE_KID,
        JESSE_JONES,
        BART_CASSIDY,
        BLACK_JACK,
        KIT_CARLSON,
        ROSE_DOOLAN,
        SID_KETCHUM,
        VULTURE_SAM
    }

    static public void populateFigure(Context context){
        XmlResourceParser xrp = context.getResources().getXml(R.xml.figure);
        availableFigures = new ArrayList<>();
        Figure figure = null;
        try{
            xrp.next();
            int eventType = xrp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && xrp.getName().equalsIgnoreCase("figure")) {
                    figure = new Figure();
                }else if(eventType == XmlPullParser.START_TAG && xrp.getName().equalsIgnoreCase("name")){
                    eventType = xrp.next();
                    figure.name=xrp.getText();
                }else if(eventType == XmlPullParser.START_TAG && xrp.getName().equalsIgnoreCase("id")){
                    eventType = xrp.next();
                    int id=Integer.parseInt(xrp.getText());
                    switch(id){
                        case 1:
                            figure.id= fig_id.SUZY_LAFAYETTE;
                            break;
                        case 2:
                            figure.id= fig_id.PAUL_REGRET;
                            break;
                        case 3:
                            figure.id= fig_id.PEDRO_RAMIREZ;
                            break;
                        case 4:
                            figure.id= fig_id.CALAMITY_JANET;
                            break;
                        case 5:
                            figure.id= fig_id.EL_GRINGO;
                            break;
                        case 6:
                            figure.id= fig_id.JOURDONNAIS;
                            break;
                        case 7:
                            figure.id= fig_id.LUCKY_DUKE;
                            break;
                        case 8:
                            figure.id= fig_id.SLAB_THE_KILLER;
                            break;
                        case 9:
                            figure.id= fig_id.WILLY_THE_KID;
                            break;
                        case 10:
                            figure.id= fig_id.JESSE_JONES;
                            break;
                        case 11:
                            figure.id= fig_id.BART_CASSIDY;
                            break;
                        case 12:
                            figure.id= fig_id.BLACK_JACK;
                            break;
                        case 13:
                            figure.id= fig_id.KIT_CARLSON;
                            break;
                        case 14:
                            figure.id= fig_id.ROSE_DOOLAN;
                            break;
                        case 15:
                            figure.id= fig_id.SID_KETCHUM;
                            break;
                        case 16:
                            figure.id= fig_id.VULTURE_SAM;
                            break;
                    }
                }else if(eventType == XmlPullParser.START_TAG && xrp.getName().equalsIgnoreCase("desc")){
                    eventType = xrp.next();
                    figure.description=xrp.getText();
                }else if(eventType == XmlPullParser.START_TAG && xrp.getName().equalsIgnoreCase("hp")){
                    eventType = xrp.next();
                    figure.baseHealthPoint=Integer.parseInt(xrp.getText());
                }else if(eventType == XmlPullParser.END_TAG && xrp.getName().equalsIgnoreCase("figure")){
                    availableFigures.add(figure);
                }
                eventType = xrp.next();
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    static public ArrayList<Figure> getAvailableFigures(Context context){
        if(availableFigures == null) {
            Figure.populateFigure(context);
        }
        return availableFigures;
    }

}
