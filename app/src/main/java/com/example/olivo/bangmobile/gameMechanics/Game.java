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

import java.lang.reflect.Array;
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
    static public Game gameInstance;

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
    boolean playerIsDying;

    public enum State {
        TURNSTART ("gameState.turnStart"),
        DYNAMITE ("gameState.dynamite"),
        JAIL ("gameState.jail"),
        PHASE1 ("gameState.phase1"),
        PHASE2("gameState.phase2"),
        ENDTURN("gameState.endturn");

        final String name;

        State(String s){
            name=s;
        }


    }

    public Game(Context context, ArrayList<Player> playersList){
        this.context=context;
        players = new HashMap<>();
        interactionStack = new ArrayDeque<>();
        setPlayersPosition(playersList);
        currentPlayer=null;
        gameInstance = this;
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
        bangLimit=1;
        throwDeque = new ArrayDeque<>();
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
                    player.handCards.add(this.getCardFromDeque());
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
            Figure.giveSpecialAttribute(players.get(playerNumber));
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
        interactionStack.addLast(new Info(currentPlayer.nextPlayer, Info.InfoType.NEXTTURN ));
        currentPlayer = currentPlayer.nextPlayer;
        state =State.TURNSTART;
        bangUsed=0;
        bangLimit=1;
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
        if(!Figure.specialPhase1(this)){
            simplePhase1Action();
        }
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
            if(currentCard != null){
                if(currentCard.actionEnded){
                    currentCard = null;
                    return getNextInteraction();
                }else{
                    currentCard.action(currentPlayer,null,this);
                    return getNextInteraction();
                }
            }else{
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
                    case ENDTURN:
                        nextTurn();
                        break;
                }
            }

        }
        if(interactionStack.isEmpty()){
            return getNextInteraction();
        }else{
            return interactionStack.pop();
        }
    }

    public void setChosenAction(Action action){
        if(playerIsDying){
            deathAction(action.player, action.selectedMove);
        }else if(this.quickDrawPending){
            Figure.luckyDuckAbility(this, action.player, (PickCardMove)action.selectedMove);
            this.quickDrawPending = false;
        }else if(action.selectedMove.type == Move.Type.GETCARD){
            GetCardMove gMove = (GetCardMove) action.selectedMove;
            for(Card c : gMove.cardToGet){
                action.player.handCards.add(c);
            }
        }else if(this.state == State.PHASE1){
            Figure.resumePhase1(this,action.selectedMove);
        }else if(currentCard == null){
            if(action.selectedMove.type == Move.Type.PLAYCARD){
                PlayMove pMove = (PlayMove) action.selectedMove;
                currentCard = pMove.playedCard;
                currentCard.actionEnded=false;
                currentCard.play(action.player, this);
            }else if((action.selectedMove.type == Move.Type.SPECIAL && ((SpecialMove) action.selectedMove).ability == SpecialMove.Ability.SIDKETCHUMABILITY)
                    ||(action.selectedMove.type == Move.Type.PICKCARD && ((PickCardMove) action.selectedMove).pickType == PickCardMove.PickType.SIDKETCHUMABILITY)){
                Figure.sidKetchumAbility(action.player,null,action.selectedMove,this);
            }else if(state == State.ENDTURN || action.selectedMove.type == Move.Type.PASS && ((PassMove) action.selectedMove).reason == PassMove.PassReason.ENDTURN){
                endTurnAction(action.selectedMove);
            }
        }else if(currentCard!=null){
            if(!currentCard.actionEnded){
                currentCard.action(action.player,action.selectedMove,this);
            }else{
                currentCard.reset();
                currentCard=null;
                setChosenAction(action);
            }
        }
    }

    ////////////////////////////////////////////
    //OTHER FUNCTIONS
    ////////////////////////////////////////////
    public void simplePhase1Action(){
        ArrayList<Card> cards = new ArrayList<>();
        cards.add(this.getCardFromDeque());
        cards.add(this.getCardFromDeque());
        ArrayList<Move> moveList = new ArrayList<>();
        moveList.add(new GetCardMove(cards));
        this.interactionStack.addLast(new Info(this.currentPlayer, Info.InfoType.PHASE1));
        this.interactionStack.addLast(new Action(this.currentPlayer,moveList));
        this.state = State.PHASE2;
    }

    public void quickDraw(Player player, ArrayList<Card.CardColor> cardColors, int cardValueMin, int cardValueMax){
        this.quickDrawPending = false;
        if(player.figure.id != Figure.fig_id.LUCKY_DUKE){
            Card card = this.getCardFromDeque();
            this.throwDeque.push(card);
            this.quickDrawResult = checkCardColorAndNumber(player, card, cardColors,  cardValueMin, cardValueMax);
        }else{
            this.quickDrawPending = true;
            this.quickDrawMin = cardValueMin;
            this.quickDrawMax = cardValueMax;
            this.quickDrawCardColors = cardColors;
            this.quickDrawPlayer = player;
            Figure.luckyDuckAbility(this, player, null);
        }
    }

    public boolean checkCardColorAndNumber(Player player, Card card , ArrayList<Card.CardColor> cardColors, int cardValueMin, int cardValueMax){
        if(cardColors.contains(card.cardColor) && card.cardValue >= cardValueMin && card.cardValue <= cardValueMax){
            interactionStack.addLast(new Info(player, Info.InfoType.QUICKDRAWWIN, card));
            return true;
        }else{
            interactionStack.addLast(new Info(player, Info.InfoType.QUICKDRAWFAIL, card));
            return false;
        }
    }

    public void endTurnAction(Move move){
        if(move.type == Move.Type.PASS){
            if(currentPlayer.handCards.size() > currentPlayer.healthPoint){
                ArrayList<Move> moveList = new ArrayList<>();
                moveList.add(new PickCardMove(currentPlayer.handCards,currentPlayer.handCards.size() - currentPlayer.healthPoint, PickCardMove.PickType.THROW ));
                interactionStack.add(new Action(currentPlayer,moveList));
            }
            state = State.ENDTURN;
        }else{
            PickCardMove pMove = (PickCardMove) move;
            for(Card c : pMove.chosenCards){
                throwDeque.push(currentPlayer.removeHandCard(c));
            }
            interactionStack.add(new Info(currentPlayer, Info.InfoType.THROW, pMove.chosenCards));
            nextTurn();
        }

    }

    public Card getCardFromDeque(){
        if(cardDeque.isEmpty()){
            shuffleThrowDeque();
        }
        return cardDeque.pop();
    }

    public void shuffleThrowDeque(){
        Random rand = new Random();
        ArrayList<Card> cardList = new ArrayList(Arrays.asList((Card[]) throwDeque.toArray()));
        while(throwDeque.size() > 1){
            Card card = cardList.remove(rand.nextInt(cardList.size()));
            throwDeque.remove(card);
            cardDeque.push(card);
        }
    }

    ////////////////////////////////////////////
    //DEATH FUNCTIONS
    ////////////////////////////////////////////
    public boolean isDying(Player player) {
        if(player.healthPoint <= 0){
            playerIsDying = true;
            deathAction(player,null);
            return true;
        }
        return false;
    }

    private void deathAction(Player player, Move move){
        ArrayList<Move> movesList = new ArrayList<>();
        Deque<Interaction> deathInteractionStack = new ArrayDeque<>();
        if(move==null){
            deathInteractionStack.addLast(new Info(player, Info.InfoType.DYING));
            int healthPointNeeded = player.healthPoint*-1+1;
            int healthPointAvailable = 0;
            ArrayList<Card> beerCards = new ArrayList<>();
            ArrayList<Card> otherCards = new ArrayList<>();

            for(Card c : player.handCards){
                if(c.id == Card.Card_id.BEER){
                    beerCards.add(c);
                }else{
                    otherCards.add(c);
                }
            }
            if(players.size()>2 && !beerCards.isEmpty()){
                healthPointAvailable+=beerCards.size();
                if(player.figure.id == Figure.fig_id.SID_KETCHUM){
                    healthPointAvailable+=Math.floor(otherCards.size()/2);
                }
                if(healthPointAvailable>=healthPointNeeded){
                    movesList.add(new SpecialMove(SpecialMove.Ability.SAVEBEER));
                    if(player.figure.id == Figure.fig_id.SID_KETCHUM){
                        movesList.add(new SpecialMove(SpecialMove.Ability.SIDKETCHUMABILITY));
                    }
                }
            }else{
                if(player.figure.id == Figure.fig_id.SID_KETCHUM){
                    healthPointAvailable+=Math.floor(player.handCards.size()/2);
                    if(healthPointAvailable>=healthPointNeeded){
                        movesList.add(new SpecialMove(SpecialMove.Ability.SIDKETCHUMABILITY));
                    }
                }
            }
            movesList.add(new PassMove(PassMove.PassReason.ENDLIFE));
            deathInteractionStack.addLast(new Action(player,movesList));
            while(!deathInteractionStack.isEmpty()){
                interactionStack.addFirst(deathInteractionStack.removeLast());
            }
        }else if(move.type == Move.Type.SPECIAL && ((SpecialMove) move).ability == SpecialMove.Ability.SIDKETCHUMABILITY){
            movesList.add(new PickCardMove(player.handCards,2, PickCardMove.PickType.SIDKETCHUMABILITY));
            deathInteractionStack.addLast(new Info(player, Info.InfoType.SIDKETCHUMABILITY));
            deathInteractionStack.addLast(new Action(player,movesList));
            while(!deathInteractionStack.isEmpty()){
                interactionStack.addFirst(deathInteractionStack.removeLast());
            }
        }else if(move.type == Move.Type.PICKCARD && ((PickCardMove) move).pickType == PickCardMove.PickType.SIDKETCHUMABILITY){
            PickCardMove pMove = (PickCardMove) move;
            for(Card c : ((PickCardMove) move).chosenCards){
                throwDeque.push(c);
            }
            player.healthPoint++;
            interactionStack.addFirst(new Info(player, Info.InfoType.SIDKETCHUMABILITY, pMove.chosenCards));
            if(player.healthPoint > 0){
                playerIsDying = false;
            }else{
                deathAction(player,null);
            }

        }else if(move.type == Move.Type.PICKCARD && ((PickCardMove) move).pickType == PickCardMove.PickType.SAVEBEER){
            PickCardMove pMove = (PickCardMove) move;
            for(Card c : ((PickCardMove) move).chosenCards){
                throwDeque.push(player.removeHandCard(c));
            }
            player.healthPoint++;
            interactionStack.addFirst(new Info(player, Info.InfoType.BEERHEAL, pMove.chosenCards));
            if(player.healthPoint > 0){
                playerIsDying = false;
            }else{
                deathAction(player,null);
            }
        }else if(move.type == Move.Type.SPECIAL && ((SpecialMove) move).ability == SpecialMove.Ability.SAVEBEER){
            ArrayList<Card> beerCards = new ArrayList<>();
            for(Card c : player.handCards){
                if(c.id == Card.Card_id.BEER){
                    beerCards.add(c);
                }
            }
            movesList.add(new PickCardMove(beerCards,1, PickCardMove.PickType.SAVEBEER));
            deathInteractionStack.addLast(new Info(player, Info.InfoType.BEERHEAL));
            deathInteractionStack.addLast(new Action(player,movesList));
            while(!deathInteractionStack.isEmpty()){
                interactionStack.addFirst(deathInteractionStack.removeLast());
            }
        }else if(move.type == Move.Type.PASS){
            playerDead(player);
        }
    }

    private void playerDead(Player player){
        interactionStack.addLast(new Info(player, Info.InfoType.DEAD));
        boolean deputy = false;
        boolean renegate = false;
        boolean outlaw = false;
        boolean sherif = false;

        for (Player p : players.values()){
            if(p != player) {
                if (p.role == Role.SHERIF) sherif = true;
                if (p.role == Role.DEPUTY) deputy = true;
                if (p.role == Role.OUTLAW) outlaw = true;
                if (p.role == Role.RENEGATE) renegate = true;
            }
        }

        if(sherif && !outlaw && !renegate){//Tous les hors la loi et les renegats sont morts. FIN DU JEU - VICTOIRE DU SHERIF ET DES ADJOINTS
            interactionStack.clear();
            interactionStack.add(new Info(player, Info.InfoType.SHERIFVICTORY));
        }else if(renegate && !sherif && !deputy && !outlaw){//Tous les adjoints et les hors la loi sont morts, le sherif vient de mourir. Reste le renegat. FIN DU JEU - VICTOIRE DU RENEGAT
            interactionStack.clear();
            interactionStack.addFirst(new Info(player, Info.InfoType.RENEGATEVICTORY));
        }else if(!sherif){//Il reste au moins un adjoint ou un hors la loi, le shérif vient de mourir. FIN DU JEU - VICTOIRE DES HORS LA LOI
            interactionStack.clear();
            interactionStack.add(new Info(player, Info.InfoType.OUTLAWVICTORY));
        }else{// Le sherif est encore vivant et il reste soit des hors la loi soit le renegat
            if(currentCard.id == Card.Card_id.DUEL || currentCard.id == Card.Card_id.BANG || currentCard.id == Card.Card_id.MISS || currentCard.id == Card.Card_id.APACHE || currentCard.id == Card.Card_id.GATLING){
                if(player != currentPlayer && player.role == Role.DEPUTY && currentPlayer.role == Role.SHERIF){
                    ArrayList<Move> moveList = new ArrayList<>();
                    moveList.add(new PickCardMove(currentPlayer.handCards, currentPlayer.handCards.size(), PickCardMove.PickType.THROWSHERIF ));
                    interactionStack.addFirst(new Action(currentPlayer, moveList));
                    interactionStack.addFirst(new Info(currentPlayer, Info.InfoType.SHERIFKILLDEPUTY, player));
                }else if(player.role == Role.OUTLAW){
                    ArrayList<Move> moveList = new ArrayList<>();
                    ArrayList<Card> cardsToGet = new ArrayList<>();
                    cardsToGet.add(this.getCardFromDeque());
                    cardsToGet.add(this.getCardFromDeque());
                    cardsToGet.add(this.getCardFromDeque());
                    moveList.add(new GetCardMove(cardsToGet));
                    interactionStack.addFirst(new Action(currentPlayer, moveList));
                    interactionStack.addFirst(new Info(currentPlayer, Info.InfoType.OUTLAWKILLED, player));
                }
            }
            if(!Figure.vultureSamAbility(player, this)){
                while(!player.handCards.isEmpty() || !player.boardCards.isEmpty()){
                    Random rand = new Random();
                    if(rand.nextBoolean()){
                        if(player.boardCards.isEmpty()){
                            throwDeque.push(player.removeRandomHandCard());
                        }else{
                            throwDeque.push(player.removeRandomBoardCard());
                        }
                    }else{
                        if(player.handCards.isEmpty()){
                            throwDeque.push(player.removeRandomBoardCard());
                        }else{
                            throwDeque.push(player.removeRandomHandCard());
                        }
                    }
                }
            }
            player.prevPlayer.nextPlayer = player.nextPlayer;
            player.nextPlayer.prevPlayer = player.prevPlayer;
            for(Map.Entry<Integer,Player> entry : players.entrySet()){
                if(entry.getValue() == player){
                    players.remove(entry.getKey());
                    break;
                }
            }
        }
        playerIsDying = false;
    }
}

