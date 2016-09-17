package com.example.olivo.bangmobile.gameMechanics;

import android.content.Context;

import com.example.olivo.bangmobile.gameMechanics.elements.cards.Card;
import com.example.olivo.bangmobile.gameMechanics.interactions.Interaction;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.Action;
import com.example.olivo.bangmobile.gameMechanics.elements.Figure;
import com.example.olivo.bangmobile.gameMechanics.elements.Player;
import com.example.olivo.bangmobile.gameMechanics.elements.Role;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.ChoiceMove;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.GetCardMove;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.Move;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.PassMove;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.PickCardMove;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.PlayMove;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.SpecialMove;
import com.example.olivo.bangmobile.gameMechanics.interactions.infos.Info;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by olivo on 07/01/2016.
 *
 */
public class Game {
    Context context;

    //GAME ATTRIBUTES
    public Map<Integer,Player> players;
    public Deque<Interaction> interactionStack;
    public Deque<Card> cardDeque;
    public Deque<Card> throwDeque;

    //QUICKDRAW ATTRIBUTES
    public boolean quickDrawPending=false;
    public boolean quickDrawResult;
    public int quickDrawMin;
    public int quickDrawMax;
    public Player quickDrawPlayer;
    public ArrayList<Card.CardColor> quickDrawCardColors;

    //TURN ATTRIBUTES
    public State state;
    public Player currentPlayer;
    public int bangUsed;
    public int bangLimit;
    Card currentCard;

    public enum State {
        TURNSTART,
        DYNAMITE,
        JAIL,
        PHASE1,
        PHASE2,
        TRASH,
        END
    }

    public Game(Context context, ArrayList<Player> playersList){
        this.context=context;
        players = new HashMap<>();
        setPlayersPosition(playersList);
        currentPlayer=null;
    }

    ////////////////////////////////////////////
    //START FUNCTIONS
    ////////////////////////////////////////////
    public void setPlayersPosition(ArrayList<Player> newPlayers){
        Random rand = new Random();
        int playerAmount = 0;
        Player prevPlayer = null;
        Player firstPlayer = null;
        while(!newPlayers.isEmpty()){
            Player newPlayer = newPlayers.remove(rand.nextInt(newPlayers.size()));
            if(prevPlayer != null){
                newPlayer.prevPlayer=prevPlayer;
                prevPlayer.nextPlayer=newPlayer;
            }
            if(firstPlayer == null){
                firstPlayer=newPlayer;
            }
            prevPlayer=newPlayer;
            players.put(playerAmount++, newPlayer);
        }
        if(firstPlayer != null){
            firstPlayer.prevPlayer=prevPlayer;
            prevPlayer.nextPlayer=firstPlayer;
        }

    }

    public void startGame(){
        this.setFigures();
        currentPlayer = this.setRoles(); //Set Role and start with Sherif
        this.createDeque();
        this.addFirstCard();
        state =State.TURNSTART;
        bangUsed=0;
        bangLimit=0;
    }

    public void addFirstCard() {
        boolean finish=false;
        int nbTurn=0;
        Player player;
        while(!finish){
            player = currentPlayer;
            finish=true;
            do{
                if(nbTurn < player.maxHealthPoint){
                    player.handCards.add(cardDeque.pop());
                    finish=false;
                }
                player=player.nextPlayer;
            }while(player != currentPlayer);
            nbTurn++;
        }


    }

    public Player setRoles(){
        ArrayList<Role> roles = GameType.getGameTypeRoles(context, players.size());
        int playerNumber=0;
        Random random = new Random();
        Player sherif=null;
        while(roles.size()!=0){
            Role role = roles.remove(random.nextInt(roles.size()));
            players.get(playerNumber).role = role;
            if(role == Role.SHERIF){
                sherif=players.get(playerNumber);
                players.get(playerNumber).healthPoint+=1;
                players.get(playerNumber).maxHealthPoint+=1;
            }
            playerNumber++;
        }
        return sherif;
    }

    public void setFigures(){
        @SuppressWarnings("unchecked")
        ArrayList<Figure> figures = (ArrayList<Figure>) Figure.getAvailableFigures(context).clone();
        int playerNumber=0;
        Random random = new Random();
        while(playerNumber < players.size()){
            Figure figure = figures.remove(random.nextInt(figures.size()));
            players.get(playerNumber).figure = figure;
            players.get(playerNumber).healthPoint += figure.baseHealthPoint;
            players.get(playerNumber).maxHealthPoint += figure.baseHealthPoint;
            switch(figure.id){
                case PAUL_REGRET:
                    players.get(playerNumber).evasion+=1;
                    break;
                case ROSE_DOOLAN:
                    players.get(playerNumber).vision+=1;
                    break;
            }
            playerNumber++;
        }
    }

    public void createDeque(){
        ArrayList<Card> baseCardList = Card.getAvailableCards(context);

        cardDeque = new ArrayDeque<>();
        Random random = new Random();

        while(baseCardList.size()>0){
            cardDeque.add(baseCardList.remove(random.nextInt(baseCardList.size())));
        }
    }

    ////////////////////////////////////////////
    //TURN FUNCTIONS
    ////////////////////////////////////////////
    public void nextTurn(){
        interactionStack.addLast(new Info(currentPlayer, Info.InfoType.NEXTTURN, currentPlayer.nextPlayer));
        currentPlayer = currentPlayer.nextPlayer;
        state =State.TURNSTART;
        bangUsed=0;
        bangLimit=0;
    }

    public void startTurn(){
        this.interactionStack.addLast(new Info(this.currentPlayer, Info.InfoType.START));
        state = State.DYNAMITE;
    }

    public void checkDynamite(){
        if(currentCard != null && currentCard.id == Card.Card_id.DYNAMITE){
            if(currentCard.actionEnded){
                currentCard.reset();
                currentCard = null;
            }else{
                currentCard.action(currentPlayer, null, this);
            }
        }else if(currentPlayer.hasCardOnBoard(Card.Card_id.DYNAMITE)){
            Card dynamiteCard =  currentPlayer.getCardFromBoard(Card.Card_id.DYNAMITE);
            dynamiteCard.action(currentPlayer, null, this);
            currentCard = dynamiteCard;
        }else{
            state = State.JAIL;
        }
    }

    public void checkJail(){
        if(currentCard != null && currentCard.id == Card.Card_id.JAIL){
            if(currentCard.actionEnded){
                currentCard.reset();
                currentCard = null;
                state = State.PHASE1;
            }else{
                currentCard.action(currentPlayer, null, this);
            }
        }else if(currentPlayer.hasCardOnBoard(Card.Card_id.JAIL)){
            Card jailCard = currentPlayer.getCardFromBoard(Card.Card_id.JAIL);
            jailCard.action(currentPlayer, null, this);
            currentCard = jailCard;
        }else{
            state = State.PHASE1;
        }
    }

    public void getPhase1(){
        Info phase1Info=null;
        ArrayList<Move> phase1Moves=new ArrayList<>();
        ArrayList<Card> cards = new ArrayList<>();
        switch(currentPlayer.figure.id){
            case BLACK_JACK:
                cards.add(cardDeque.pop());
                Card bonusCard = cardDeque.pop();
                cards.add(bonusCard);
                if(this.checkCardColorAndNumber(bonusCard, new ArrayList<>(Arrays.asList(new Card.CardColor[]{Card.CardColor.HEART, Card.CardColor.DIAMOND})), 1 , 13)){
                    phase1Info = new Info(currentPlayer, Info.InfoType.BLACKJACKBONUSWIN, bonusCard);
                    cards.add(cardDeque.pop());
                }else{
                    phase1Info = new Info(currentPlayer, Info.InfoType.BLACKJACKBONUSFAIL);
                }
                phase1Moves.add(new GetCardMove(cards));
                state=State.PHASE2;
                break;
            case KIT_CARLSON:
                cards.add(cardDeque.pop());
                cards.add(cardDeque.pop());
                cards.add(cardDeque.pop());
                phase1Moves.add(new PickCardMove(cards,2, PickCardMove.PickType.KITCARLSONPHASE1));
                break;
            case PEDRO_RAMIREZ:
                phase1Moves.add(new ChoiceMove(ChoiceMove.Choice.PEDRORAMIREZPHASE1));
                break;
            case JESSE_JONES:
                phase1Moves.add(new ChoiceMove(ChoiceMove.Choice.JESSEJONESPHASE1));
                break;
            default:
                phase1Info = new Info(currentPlayer, Info.InfoType.PHASE1);
                cards.add(cardDeque.pop());
                cards.add(cardDeque.pop());
                phase1Moves.add(new GetCardMove(cards));
                break;
        }
        interactionStack.addLast(phase1Info);
        interactionStack.addLast(new Action(currentPlayer, phase1Moves));
    }

    public void getPhase2(){
        this.interactionStack.addLast(new Info(this.currentPlayer, Info.InfoType.PHASE2PLAY));
        ArrayList<Move> phase2Moves=new ArrayList<>();
        ArrayList<Card> availableCards=new ArrayList<>();
        ArrayList<Card> disabledCards=new ArrayList<>();
        for(Card card : this.currentPlayer.handCards){
            if(card.usable(this.currentPlayer,this)){
                availableCards.add(card);
            }else{
                disabledCards.add(card);
            }
        }
        phase2Moves.add(new PlayMove(availableCards, disabledCards));
        Figure.sidKetchumAbility(this.currentPlayer, phase2Moves,null,this);
        phase2Moves.add(new PassMove(PassMove.PassReason.ENDTURN));
        interactionStack.addLast(new Action(this.currentPlayer, phase2Moves));
    }

    ////////////////////////////////////////////
    //INTERACTIONS FUNCTIONS
    ////////////////////////////////////////////
    public Interaction getNextInteraction(){
        if(interactionStack.isEmpty()){
            switch(this.state){
                case TURNSTART:
                    this.startTurn();
                    break;
                case DYNAMITE:
                    this.checkDynamite();
                    break;
                case JAIL:
                    this.checkJail();
                    break;
                case PHASE1:
                    this.getPhase1();
                    break;
                case PHASE2:
                    this.getPhase2();
                    break;
            }
        }
        return interactionStack.pop();
    }

    public void setChosenAction(Action action){
        if(this.quickDrawPending){
            Figure.luckyDuckAbility(this, action.player, (PickCardMove)action.selectedMove);
            this.quickDrawPending = false;
        }else if(this.state == State.PHASE1){
            Figure.resumePhase1(this,action.selectedMove);
        }else if(currentCard == null){
            if(action.selectedMove.type == Move.Type.PLAYCARD){
                PlayMove pMove = (PlayMove) action.selectedMove;
                currentCard = pMove.playedCard;
                currentCard.play(currentPlayer, this);
            }else if((action.selectedMove.type == Move.Type.SPECIAL && ((SpecialMove) action.selectedMove).ability == SpecialMove.Ability.SIDKETCHUMABILITY)
                    ||(action.selectedMove.type == Move.Type.PICKCARD && ((PickCardMove) action.selectedMove).pickType == PickCardMove.PickType.SIDKETCHUMABILITY)){
                Figure.sidKetchumAbility(action.player,null,action.selectedMove,this);
            }else if(action.selectedMove.type == Move.Type.PASS && ((PassMove) action.selectedMove).reason == PassMove.PassReason.ENDTURN){
                this.nextTurn();
            }
        }else if(!currentCard.actionEnded){
            currentCard.action(action.player,action.selectedMove,this);
        }
    }

    ////////////////////////////////////////////
    //OTHER FUNCTIONS
    ////////////////////////////////////////////
    public void simplePhase1Action(){
        ArrayList<Card> cards = new ArrayList<>();
        cards.add(this.cardDeque.pop());
        cards.add(this.cardDeque.pop());
        ArrayList<Move> moveList = new ArrayList<>();
        moveList.add(new GetCardMove(cards));
        this.interactionStack.addLast(new Info(this.currentPlayer, Info.InfoType.PHASE1));
        this.interactionStack.addLast(new Action(this.currentPlayer,moveList));
        this.state = State.PHASE2;
    }

    public void quickDraw(Player player, ArrayList<Card.CardColor> cardColors, int cardValueMin, int cardValueMax){
        this.quickDrawPending = false;
        if(player.figure.id != Figure.fig_id.LUCKY_DUKE){
            Card card = this.cardDeque.pop();
            this.throwDeque.push(card);
            this.quickDrawResult = checkCardColorAndNumber(card, cardColors,  cardValueMin, cardValueMax);
        }else{
            this.quickDrawPending = true;
            this.quickDrawMin = cardValueMin;
            this.quickDrawMax = cardValueMax;
            this.quickDrawCardColors = cardColors;
            this.quickDrawPlayer = player;
            Figure.luckyDuckAbility(this, player, null);
        }
    }

    public boolean checkCardColorAndNumber(Card card , ArrayList<Card.CardColor> cardColors, int cardValueMin, int cardValueMax){
        if(cardColors.contains(card.cardColor) && card.cardValue >= cardValueMin && card.cardValue <= cardValueMax){
            interactionStack.addLast(new Info(null, Info.InfoType.QUICKDRAWWIN, card));
            return true;
        }else{
            interactionStack.addLast(new Info(null, Info.InfoType.QUICKDRAWFAIL, card));
            return false;
        }
    }

    public boolean isDying(Player player) {
        if(player.healthPoint <= 0){
            Deque<Interaction> deathInteractionStack = new ArrayDeque<>();
            deathInteractionStack.addLast(new Info(player, Info.InfoType.DYING));
            ArrayList<Move> movesList = new ArrayList<>();
            if(players.values().size()>2){
                if(player.hasAmountOfCardInHand(Card.Card_id.BEER,(player.healthPoint*-1+1))){
                    movesList.add(new SpecialMove(SpecialMove.Ability.SAVEBEER));
                }
            }
            if(player.handCards.size()>=(player.healthPoint*-1+1)*2 && player.figure.id == Figure.fig_id.SID_KETCHUM){
                movesList.add(new PickCardMove(player.handCards,(player.healthPoint*-1+1)*2, PickCardMove.PickType.HEALTHROW));
            }
            movesList.add(new PassMove(PassMove.PassReason.ENDLIFE));
            deathInteractionStack.addLast(new Action(player,movesList));
            while(!deathInteractionStack.isEmpty()){
                interactionStack.addFirst(deathInteractionStack.removeLast());
            }
            return true;
        }
        return false;
    }

}

