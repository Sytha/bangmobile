package com.example.olivo.bangmobile.consoleInterfaceSolo;

import android.content.Context;

import com.example.olivo.bangmobile.MainActivity;
import com.example.olivo.bangmobile.gameMechanics.Game;
import com.example.olivo.bangmobile.gameMechanics.elements.cards.Card;
import com.example.olivo.bangmobile.gameMechanics.elements.Player;
import com.example.olivo.bangmobile.gameMechanics.elements.Role;
import com.example.olivo.bangmobile.gameMechanics.interactions.Interaction;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.Action;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.ChoiceMove;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.GetCardMove;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.Move;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.PassMove;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.PickCardMove;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.PlayMove;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.SpecialMove;
import com.example.olivo.bangmobile.gameMechanics.interactions.infos.Info;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by olivo on 10/08/2016.
 */
public class ConsoleInterface {
    Game game;
    Context context;
    HashMap<Integer,Move> moveList;
    Action currentAction;
    MainActivity activity;
    public ConsoleInterface(Context context,MainActivity activity){
        ArrayList<Player> players = new ArrayList<>();
        players.add(new Player(0, "p1"));
        players.add(new Player(1, "p2"));
        players.add(new Player(2, "p3"));
        players.add(new Player(3, "p4"));
        this.context=context;
        game = new Game(context, players );
        game.startGame();
        this.activity = activity;
        moveList = new HashMap<>();
    }



    public String getSecretPlayersInfo(){
        String info = "";
        for(Map.Entry e : game.players.entrySet()){
            Player p =  (Player) e.getValue();
            info += "\npos :" + e.getKey() + " info : " + p.id + ", " + p.figure.id + ", "+ p.role + ", " + p.healthPoint + "/" + p.maxHealthPoint ;
        }

        return info;
    }

    public String getPlayersInfo(){
        String info = "Players infos : \n";
        for(Map.Entry e : game.players.entrySet()){
            Player p =  (Player) e.getValue();
            info += "\n" + p.name + "("+p.figure.id +")";
            if(p.role == Role.SHERIF){
                info += " (SHERIF)";
            }
            info += "\nHP :" + p.healthPoint + "/" + p.maxHealthPoint;
            info += " Vision :" + p.vision;
            info += " WVision :" + p.weaponVision;
            info += " Evasion :" + p.evasion;
            info += "\nHand : " + p.handCards.size() + "(";
            String add ="";
            for(Card card : p.handCards){
                info += add+card.id+"("+card.cardValue+"-"+card.cardColor+")";
                add=", ";
            }
            info += ")\n";
        }

        return info;
    }

    public String getGameInfo(){
        String textToDisplay = "\n\nGame infos : \n";
        boolean isAction = false;
        Interaction interaction;

        while(!isAction){
            interaction = this.getNextInteraction();
            if(interaction.type == Interaction.Types.INFO){
                Info info = (Info) interaction;
                textToDisplay += "\n" + info.player.name + " - " + info.info;
            }else{
                isAction = true;
                currentAction = (Action) interaction;
            }
        }
        textToDisplay+="\n\n";
        return textToDisplay;
    }

    public void getActionList(){
        HashMap<Integer,String> actionlist = new HashMap<>();
        int cpt = 0;
        for(Move move : currentAction.availableMoves){
            switch(move.type){
                case TARGET:
                    break;
                case PLAYCARD:
                    PlayMove pMove = (PlayMove) move;
                    moveList.put(cpt,pMove);
                    activity.displaySimpleActionButton("Jouer une carte", cpt++);
                    break;
                case CHOICE:
                    ChoiceMove cMove = (ChoiceMove) move;
                    moveList.put(cpt, cMove);
                    activity.displaySimpleActionButton(cMove.choice.toString(), cpt++);
                    break;
                case GETCARD:
                    GetCardMove gMove = (GetCardMove) move;
                    moveList.put(cpt,gMove);
                    activity.displaySimpleActionButton("Piocher " + gMove.cardToGet.size() + " cartes", cpt++);
                    break;
                case PASS:
                    PassMove passMove = (PassMove) move;
                    moveList.put(cpt,passMove);
                    activity.displaySimpleActionButton(passMove.reason.toString(), cpt++);
                    break;
                case PICKCARD:
                    PickCardMove pcMove = (PickCardMove) move;
                    moveList.put(cpt,pcMove);
                    activity.displaySimpleActionButton(pcMove.pickType.toString(), cpt++);
                    break;
                case SPECIAL:
                    SpecialMove specialMove = (SpecialMove) move;
                    moveList.put(cpt, specialMove);
                    activity.displaySimpleActionButton(specialMove.ability.toString(), cpt++);
                    break;
            }
        }
    }

    public void selectSimpleMove(int moveId){
        Move move = moveList.get(moveId);
        if(move.type == Move.Type.PASS || move.type == Move.Type.GETCARD || move.type == Move.Type.SPECIAL){
            currentAction.selectMove(moveList.get(moveId));
            game.setChosenAction(currentAction);
            activity.displayInfoAndControl();
        }else{
            PickCardMove pMove = (PickCardMove) move;
            HashMap<Integer,String> cardList = new HashMap<>();
            int cardamount = 0;
            for(Card c : ((PickCardMove) move).cardsToGet){
                cardList.put(cardamount++, c.id.toString());
            }
            activity.displayPickCardMove(pMove.pickType.toString(), cardList, pMove.amountToGet);
        }
    }




    public String getCardsList(){
        String info="";
        for(Card card : game.cardDeque){
            info+="\n"+card.id;
        }
        return info;
    }

    public Interaction getNextInteraction(){
        return game.getNextInteraction();
    }

}
