package com.example.olivo.bangmobile.gameMechanics;

import android.content.Context;
import android.content.res.XmlResourceParser;

import com.example.olivo.bangmobile.R;
import com.example.olivo.bangmobile.gameMechanics.elements.Role;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by olivo on 11/01/2016.
 */



public class GameType {
    public static HashMap<Integer,ArrayList<Role>> rolesByGameTypes ;

    public static HashMap<Integer,ArrayList<Role>> getRolesByGameTypes(Context context){
        if(rolesByGameTypes ==null){
            XmlResourceParser xrp = context.getResources().getXml(R.xml.role);
            rolesByGameTypes = new HashMap<>();
            ArrayList<Role> roles = new ArrayList<>();
            try {
                xrp.next();
                int eventType = xrp.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG && xrp.getName().equalsIgnoreCase("gametype")) {
                        roles = new ArrayList<>();
                    }  else if (eventType == XmlPullParser.START_TAG && xrp.getName().equalsIgnoreCase("sherif")) {
                        xrp.next();
                        for (int i = 0; i < Integer.parseInt(xrp.getText()); i++) {
                            roles.add(Role.SHERIF);
                        }
                    } else if (eventType == XmlPullParser.START_TAG && xrp.getName().equalsIgnoreCase("outlaw")) {
                        xrp.next();
                        for (int i = 0; i < Integer.parseInt(xrp.getText()); i++) {
                            roles.add(Role.OUTLAW);
                        }
                    } else if (eventType == XmlPullParser.START_TAG && xrp.getName().equalsIgnoreCase("deputy")) {
                        xrp.next();
                        for (int i = 0; i < Integer.parseInt(xrp.getText()); i++) {
                            roles.add(Role.DEPUTY);
                        }
                    } else if (eventType == XmlPullParser.START_TAG && xrp.getName().equalsIgnoreCase("renegate")) {
                        xrp.next();
                        for (int i = 0; i < Integer.parseInt(xrp.getText()); i++) {
                            roles.add(Role.RENEGATE);
                        }
                    } else if (eventType == XmlPullParser.END_TAG && xrp.getName().equalsIgnoreCase("gametype")) {
                        rolesByGameTypes.put(roles.size(), roles);
                    }
                    eventType = xrp.next();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
        }
        return rolesByGameTypes;
    }

    public  static ArrayList<Role> getGameTypeRoles(Context context, int playersNumber){
        getRolesByGameTypes(context);
        return (ArrayList<Role>) rolesByGameTypes.get(playersNumber).clone();
    }
}
