package com.example.olivo.bangmobile.gameMechanics.state;

import com.example.olivo.bangmobile.gameMechanics.elements.cards.Card;
import com.example.olivo.bangmobile.gameMechanics.elements.Figure;
import com.example.olivo.bangmobile.gameMechanics.elements.Player;
import com.example.olivo.bangmobile.gameMechanics.elements.Role;

import java.util.ArrayList;

/**
 * Created by olivo on 16/03/2016.
 */
public class PlayerState {
    public int id;
    public String name;
    public Role role;
    public Figure figure;
    public int handCards;
    public ArrayList<Card> boardCards;
    public int maxHealthPoint;
    public int healthPoint;
    public int evasion = 0;
    public int vision = 1;
    public int weaponVision = 1;
    public Player nextPlayer;
    public Player prevPlayer;
}
