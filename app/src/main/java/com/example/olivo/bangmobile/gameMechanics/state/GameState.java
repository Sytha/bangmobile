package com.example.olivo.bangmobile.gameMechanics.state;

import com.example.olivo.bangmobile.gameMechanics.elements.cards.Card;
import com.example.olivo.bangmobile.gameMechanics.elements.Player;

import java.util.ArrayList;
import java.util.Deque;

/**
 * Created by olivo on 03/03/2016.
 */
public class GameState {
    public Deque<Card> cardDeque;
    public Deque<Card> throwDeque;
    public Player player;
    public ArrayList<PlayerState> playerStates;



}
