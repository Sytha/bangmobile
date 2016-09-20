package com.example.olivo.bangmobile.consoleInterfaceSolo;

import android.content.Context;

import com.example.olivo.bangmobile.MainActivity;
import com.example.olivo.bangmobile.gameMechanics.Game;
import com.example.olivo.bangmobile.gameMechanics.elements.cards.Card;
import com.example.olivo.bangmobile.gameMechanics.elements.Player;
import com.example.olivo.bangmobile.gameMechanics.interactions.Interaction;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.Action;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.ChoiceMove;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.GetCardMove;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.Move;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.PassMove;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.PickCardMove;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.PlayMove;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.SpecialMove;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.TargetMove;
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
    HashMap<Integer,Card> pickCardList;
    HashMap<Integer,Boolean> pickCardChecked;
    Move selectedMove;
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
            info += "("+p.role+") ";
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
        String textToDisplay = "";
        boolean isAction = false;
        Interaction interaction;

        while(!isAction){
            interaction = this.getNextInteraction();
            if(interaction.type == Interaction.Types.INFO){
                Info info = (Info) interaction;
                textToDisplay +=  info.player.name + " - " + info.info + "\n";
            }else{
                isAction = true;
                currentAction = (Action) interaction;
            }
        }
        return textToDisplay;
    }

    public void getActionList(){
        HashMap<Integer,String> actionlist = new HashMap<>();
        int cpt = 0;
        Move currentMove;
        moveList = new HashMap<>();
        for(Move move : currentAction.availableMoves){
            currentMove = move;
            switch(move.type){
                case TARGET:
                    TargetMove tMove = (TargetMove) move;
                    moveList.put(cpt,tMove);
                    activity.displaySimpleActionButton("Sélectionner une cible", cpt++);
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
        if(moveList.size() == 1 && moveList.get(0).type != Move.Type.GETCARD){
            selectMove(0);
        }
    }

    public void selectMove(int moveId){
        Move move = moveList.get(moveId);
        if(move.type == Move.Type.PASS || move.type == Move.Type.GETCARD || move.type == Move.Type.SPECIAL){
            currentAction.selectMove(moveList.get(moveId));
            game.setChosenAction(currentAction);
            activity.displayInfoAndControl();
        }else if(move.type == Move.Type.PICKCARD){
            selectedMove = move;
            PickCardMove pMove = (PickCardMove) move;
            pickCardList = new HashMap<>();
            pickCardChecked = new HashMap<>();
            HashMap<Integer,String> cardList = new HashMap<>();
            int cardamount = 0;
            for(Card c : ((PickCardMove) move).cardsToGet){
                cardList.put(cardamount, c.id.toString());
                pickCardList.put(cardamount,c);
                pickCardChecked.put(cardamount++,false);
            }
            activity.displayCheckBoxMove(pMove.pickType.toString() + " " + pMove.amountToGet + " cards\n", cardList, pMove.amountToGet);
        }
    }

    public void pickCard(int idCard, boolean pickValue){
        pickCardChecked.put(idCard, pickValue);
    }

    public void selectPickCardMove(){
        PickCardMove move = (PickCardMove) selectedMove;
        ArrayList<Card> cardChosen = new ArrayList<>();
        for(int i=0; i<pickCardChecked.size() ;i++){
            if(pickCardChecked.get(i)){
                cardChosen.add(pickCardList.get(i));
            }
        }
        move.chooseCards(cardChosen);
        currentAction.selectMove(selectedMove);
        game.setChosenAction(currentAction);
        activity.displayInfoAndControl();
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
