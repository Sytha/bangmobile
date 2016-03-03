package com.example.olivo.bangmobile.gameMechanics.elements;

import com.example.olivo.bangmobile.gameMechanics.interactions.Interaction;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.Action;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.ChoiceMove;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.Move;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.PassMove;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.PickCardMove;
import com.example.olivo.bangmobile.gameMechanics.interactions.infos.Info;
import com.example.olivo.bangmobile.gameMechanics.elements.Card.Card_id;
import com.example.olivo.bangmobile.gameMechanics.elements.Figure.fig_id;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;


/**
 * Created by olivo on 05/01/2016.
 */
public class Turn {
    Player currentPlayer;
    Map<Integer,Player>  playersList;
    State state;
    boolean bangUsed;
    Deque<Interaction> interactionStack;
    Action currentAction;

    public Turn(Player currentPlayer,Map<Integer,Player> playersList) {
        this.currentPlayer = currentPlayer;
        state=State.START;
        bangUsed=false;
        interactionStack=new LinkedList<>();
        this.playersList=playersList;
    }

    public enum State{
        START,
        DYNAMITE,
        JAIL,
        DYING,
        PHASE1,
        P2PLAY,
        TRASH,
        END
    }

    public Interaction getNextInteraction(){
        if(interactionStack.isEmpty()){
            switch(state){
                case START:
                    startTurn();
                    break;
                case DYNAMITE:
                    checkDynamite();
                    break;
                case JAIL:
                    checkJail();
                    break;
                case PHASE1:
                    getPhase1();

            }
        }
        return interactionStack.pop();
    }

    ////////////////////////////////////////////////
    //ACTION FUNCTIONS
    ////////////////////////////////////////////////

    public void startTurn(){
        interactionStack.addLast(new Info(currentPlayer, Info.InfoType.START));
        state = State.DYNAMITE;
    }

    public void checkDynamite(){
        boolean detonate=false;
        interactionStack.addLast(new Info(currentPlayer, Info.InfoType.CHECK_DYNAMITE));
        if(currentPlayer.hasCardOnBoard(Card_id.DYNAMITE)){
            if(quickDraw(8)) {
                detonate = currentPlayer.figure.id != fig_id.LUCKY_DUKE || quickDraw(8);
            }
            if(detonate){
                currentPlayer.healthPoint-=3;
                currentPlayer.removeBoardCard(Card_id.DYNAMITE);
                interactionStack.addLast(new Info(currentPlayer, Info.InfoType.DYNAMITE_EXPLODED));
                checkMort(currentPlayer);
            }else {
                interactionStack.addLast(new Info(currentPlayer, Info.InfoType.DYNAMITE_THROWED));
                currentPlayer.nextPlayer.addBoardCard(currentPlayer.removeBoardCard(Card_id.DYNAMITE));
            }
        }
        state = State.JAIL;

    }

    public void checkJail(){
        boolean jail=false;
        interactionStack.addLast(new Info(currentPlayer, Info.InfoType.CHECK_JAIL));
        if(currentPlayer.hasCardOnBoard(Card_id.PRISON)){
            if(quickDraw(4)){
                if(currentPlayer.figure.id == fig_id.LUCKY_DUKE){
                    jail=quickDraw(4);
                }else{
                    jail=true;
                }
            }
            currentPlayer.removeBoardCard(Card_id.PRISON);
            if(jail){
                interactionStack.addLast(new Info(currentPlayer, Info.InfoType.JAIL_EVADE));
                state=State.PHASE1;
            }else{
                interactionStack.addLast(new Info(currentPlayer, Info.InfoType.JAIL_STAY));
                state=State.END;
            }
        }
    }

    public void getPhase1(){
        switch(currentPlayer.figure.id){
            case BLACK_JACK:
                if(quickDraw(4)){

                }
                break;
            case KIT_CARLSON:
                break;
            case PEDRO_RAMIREZ:
                break;
            case JESSE_JONES:
                break;
            default:
                break;
        }
    }

    private void checkMort(Player player) {
        if(player.healthPoint <= 0){
            state = State.DYING;
            interactionStack.addLast(new Info(player, Info.InfoType.DYING));
            ArrayList<Move> movesList = new ArrayList<>();
            if(playersList.size()>2){
                if(player.hasAmountOfCardInHand(Card_id.BIERE,(player.healthPoint*-1+1))){
                    movesList.add(new ChoiceMove(ChoiceMove.Choice.SAVEBEER));
                }
            }
            if(player.handCards.size()>=(player.healthPoint*-1+1)*2 && player.figure.id == fig_id.SID_KETCHUM){
                movesList.add(new PickCardMove(player.handCards,(player.healthPoint*-1+1)*2, PickCardMove.PickType.SAVETHROW));
            }
            movesList.add(new PassMove());
            interactionStack.addFirst(new Action(player,movesList));
        }
    }

    public static boolean quickDraw(int max){
        Random rand = new Random();
        return (rand.nextInt(max)==(max-1));
    }


    public static ArrayList<Integer> canBang(Integer posPlayer,HashMap<Integer,Player> playersList){
        Player player = playersList.get(posPlayer);
        int playerVision = player.vision+player.weaponVision;
        int maxPos = (int) Math.floor((double) playersList.size());
        ArrayList<Integer> positionAvailable = new ArrayList<>();

        for(int distance = 1 ;distance<=maxPos && distance <=playerVision ;distance++){
            Player sib1  =  playersList.get((posPlayer+distance)%playersList.size());
            Player sib2  =  playersList.get((posPlayer-distance)%playersList.size());
            if(distance + sib1.evasion <= playerVision){
                positionAvailable.add((posPlayer+distance)%playersList.size());
            }
            if(distance + sib2.evasion <= playerVision){
                positionAvailable.add((posPlayer-distance)%playersList.size());
            }
        }
        return positionAvailable;
    }

    public static void playerGetCardFromDeque(ArrayDeque<Card> deque,Player player, int number){
        for(int i = 0; i<number;i++)player.addHandCard(deque.remove());
    }

    public static void playerGetCardFromPlayer(Player player, Player target, int number){
            player.addHandCard(target.removeRandomHandCard());
    }

/*
    public static String playCard(Player player, Card card){
        player.removeHandCard(card.id);
        if(card.type==Card.WEAPON){
            int cardId = player.hasWeaponOnBoard();
            if(cardId >= 0){
                player.removeBoardCard(cardId);
            }
            player.addBoardCard(card);
            return "toto";
        }else if(card.type==Card.ABILITY){
            if(card.id != 17){
                player.addBoardCard(card);
            }
        }
        return "toto";
    }
*/



}
