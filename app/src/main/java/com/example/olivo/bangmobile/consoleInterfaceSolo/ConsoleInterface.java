package com.example.olivo.bangmobile.consoleInterfaceSolo;

import android.content.Context;
import android.widget.Toast;

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
    HashMap<Integer,Card> cardList;
    HashMap<Integer,ChoiceMove.Answer> answerList;
    HashMap<Integer,Player> playerList;
    HashMap<Integer,Boolean> checkedList;
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
        for(Player p : game.players.values()){
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
            info += "\nBoard : " + p.boardCards.size() + "(";
            add ="";
            for(Card card : p.boardCards){
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
            interaction = game.getNextInteraction();
            if(interaction.type == Interaction.Types.INFO){
                Info info = (Info) interaction;
                if(info.player != null){
                    textToDisplay +=  info.player.name + " - " + info.info + "\n";
                }

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
        activity.clearButtonList();
        for(Move move : currentAction.availableMoves){
            currentMove = move;
            switch(move.type){
                case TARGET:
                    TargetMove tMove = (TargetMove) move;
                    moveList.put(cpt,tMove);
                    activity.displaySimpleActionButton(currentAction.player.name + " - Sélectionner une cible", cpt++);
                    break;
                case PLAYCARD:
                    PlayMove pMove = (PlayMove) move;
                    moveList.put(cpt,pMove);
                    activity.displaySimpleActionButton(currentAction.player.name + " - Jouer une carte", cpt++);
                    break;
                case CHOICE:
                    ChoiceMove cMove = (ChoiceMove) move;
                    moveList.put(cpt, cMove);
                    activity.displaySimpleActionButton(currentAction.player.name + " - " + cMove.choice.toString(), cpt++);
                    break;
                case GETCARD:
                    GetCardMove gMove = (GetCardMove) move;
                    moveList.put(cpt,gMove);
                    activity.displaySimpleActionButton(currentAction.player.name + " - Piocher " + gMove.cardToGet.size() + " cartes", cpt++);
                    break;
                case PASS:
                    PassMove passMove = (PassMove) move;
                    moveList.put(cpt, passMove);
                    activity.displaySimpleActionButton(currentAction.player.name + " - " +passMove.reason.toString(), cpt++);
                    break;
                case PICKCARD:
                    PickCardMove pcMove = (PickCardMove) move;
                    moveList.put(cpt,pcMove);
                    activity.displaySimpleActionButton(currentAction.player.name + " - " +pcMove.pickType.toString(), cpt++);
                    break;
                case SPECIAL:
                    SpecialMove specialMove = (SpecialMove) move;
                    moveList.put(cpt, specialMove);
                    activity.displaySimpleActionButton(currentAction.player.name + " - " +specialMove.ability.toString(), cpt++);
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
            cardList = new HashMap<>();
            checkedList = new HashMap<>();
            HashMap<Integer,String> stringCardList = new HashMap<>();
            int cardamount = 0;
            for(Card c : ((PickCardMove) move).cardsToGet){
                stringCardList.put(cardamount, c.id.toString());
                this.cardList.put(cardamount,c);
                checkedList.put(cardamount++,false);
            }
            activity.displayCheckBoxPickCardMove(currentAction.player.name + " - " +pMove.pickType.toString() + " " + pMove.amountToGet + " cards\n", stringCardList, pMove.amountToGet);
        }else if(move.type == Move.Type.CHOICE){
            selectedMove = move;
            ChoiceMove cMove = (ChoiceMove) move;
            HashMap<Integer,String> choiceStringList = new HashMap<>();
            answerList = new HashMap<>();
            int answerCpt = 0;
            for(ChoiceMove.Answer answer: cMove.availableAnswer){
                choiceStringList.put(answerCpt,answer.toString());
                answerList.put(answerCpt++, answer);
            }
            activity.displayButtonChoiceMove(currentAction.player.name + " - " +cMove.choice.toString(),choiceStringList);
        }else if(move.type == Move.Type.PLAYCARD){
            selectedMove = move;
            PlayMove pMove = (PlayMove) move;
            cardList = new HashMap<>();
            HashMap<Integer,String> stringCardList = new HashMap<>();
            int answerCpt = 0;
            for(Card c: pMove.availableCards){
                cardList.put(answerCpt, c);
                stringCardList.put(answerCpt++,c.id.toString());
            }
            activity.displayButtonPlayCardMove(currentAction.player.name + " - " +"Play a card",stringCardList);
        }else if(move.type == Move.Type.TARGET){
            selectedMove = move;
            TargetMove tMove = (TargetMove) move;
            playerList = new HashMap<>();
            HashMap<Integer,String> stringPlayerList = new HashMap<>();
            int playerCpt = 0;
            for(Player p: tMove.availableTargets){
                playerList.put(playerCpt, p);
                stringPlayerList.put(playerCpt++,p.name);
            }
            activity.displayButtonTargetMove(currentAction.player.name + " - " + "Target a player", stringPlayerList);
        }
    }

    public void selectPickCardMove(){
        PickCardMove move = (PickCardMove) selectedMove;
        ArrayList<Card> cardChosen = new ArrayList<>();
        for(int i = 0; i< checkedList.size() ; i++){
            if(checkedList.get(i)){
                cardChosen.add(cardList.get(i));
            }
        }
        move.chooseCards(cardChosen);
        currentAction.selectMove(selectedMove);
        game.setChosenAction(currentAction);
        activity.displayInfoAndControl();
    }

    public void pickCard(int idCard, boolean pickValue){
        checkedList.put(idCard, pickValue);
    }

    public void selectAnswer(Integer key) {
        ChoiceMove move = (ChoiceMove) selectedMove;
        move.select(answerList.get(key));
        currentAction.selectMove(selectedMove);
        game.setChosenAction(currentAction);
        activity.displayInfoAndControl();
    }

    public void selectCard(Integer key) {
        PlayMove move = (PlayMove) selectedMove;
        move.playedCard=cardList.get(key);
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

    public void selectPlayer(Integer key) {
        TargetMove move = (TargetMove) selectedMove;
        move.selectedPlayer=playerList.get(key);
        currentAction.selectMove(selectedMove);
        game.setChosenAction(currentAction);
        activity.displayInfoAndControl();
    }
}
