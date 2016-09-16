package com.example.olivo.bangmobile.gameMechanics.elements;

import com.example.olivo.bangmobile.gameMechanics.Game;
import com.example.olivo.bangmobile.gameMechanics.elements.cards.Card;
import com.example.olivo.bangmobile.gameMechanics.elements.cards.CardDynamite;
import com.example.olivo.bangmobile.gameMechanics.interactions.Interaction;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.Action;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.ChoiceMove;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.GetCardMove;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.Move;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.PassMove;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.PickCardMove;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.PlayMove;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.TargetMove;
import com.example.olivo.bangmobile.gameMechanics.interactions.infos.Info;
import com.example.olivo.bangmobile.gameMechanics.elements.cards.Card.Card_id;
import com.example.olivo.bangmobile.gameMechanics.elements.Figure.fig_id;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;


/**
 * Created by olivo on 05/01/2016.
 */
public class Turn {
    Player currentPlayer;
    Map<Integer,Player>  playersList;
    public State state;
    public boolean bangUsed;
    Deque<Interaction> interactionStack;
    public Deque<Card> cardDeque;
    public Deque<Card> throwDeque;
    Map<TargetMove.TargetType, Player> targets = new HashMap<>();
    Card_id cardPlayed;
    public Game game;
    Card currentCard;

    public enum State{
        START,
        DYNAMITE,
        JAIL,
        PHASE1,
        PHASE2,
        TRASH,
        END
    }

    public Turn(Player currentPlayer,Map<Integer,Player> playersList, Game game) {
        this.currentPlayer = currentPlayer;
        state=State.START;
        bangUsed=false;
        interactionStack=new LinkedList<>();
        this.playersList=playersList;
        this.game = game;
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
                    break;
                case PHASE2:
                    if(!checkSuziLafayette()){
                        getPhase2();
                    }
                    break;

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
        if(currentPlayer.hasCardOnBoard(Card_id.DYNAMITE)){
            CardDynamite dynamiteCard = (CardDynamite) currentPlayer.getCardFromBoard(Card_id.DYNAMITE);
            dynamiteCard.action(currentPlayer, null, game);
            currentCard = dynamiteCard;
        }else{
            state = State.JAIL;
        }
    }

    public void checkJail(){
        if(currentPlayer.hasCardOnBoard(Card_id.JAIL)){
            CardDynamite dynamiteCard = (CardDynamite) currentPlayer.getCardFromBoard(Card_id.JAIL);
            dynamiteCard.action(currentPlayer, null, game);
            currentCard = dynamiteCard;
        }else{
            state = State.JAIL;
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
                if(game.checkCardColorAndNumber(bonusCard, new ArrayList(Arrays.asList(new Card.CardColor[]{Card.CardColor.HEART, Card.CardColor.DIAMOND})), 1 , 13)){
                    phase1Info = new Info(currentPlayer, Info.InfoType.PHASE1BONUS, bonusCard);
                    cards.add(cardDeque.pop());
                }else{
                    phase1Info = new Info(currentPlayer, Info.InfoType.PHASE1BONUSFAIL);
                }
                phase1Moves.add(new GetCardMove(cards));
                state=State.PHASE2;
                break;
            case KIT_CARLSON:
                cards.add(cardDeque.pop());
                cards.add(cardDeque.pop());
                cards.add(cardDeque.pop());
                phase1Moves.add(new PickCardMove(cards,2, PickCardMove.PickType.PHASE1CHOOSE));
                break;
            case PEDRO_RAMIREZ:
                phase1Moves.add(new ChoiceMove(ChoiceMove.Choice.PICKTRASH));
                break;
            case JESSE_JONES:
                phase1Moves.add(new ChoiceMove(ChoiceMove.Choice.PICKPLAYER));
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
        Info phase1Info = new Info(currentPlayer, Info.InfoType.PHASE2PLAY);
        ArrayList<Move> phase2Moves=new ArrayList<>();
        ArrayList<Card> availableCards=currentPlayer.handCards;
        ArrayList<Card> disabledCards=new ArrayList<>();
        for(Card card : currentPlayer.handCards){
            if((card.type == Card.Card_type.WEAPON || card.type == Card.Card_type.ABILITY) && currentPlayer.hasCardOnBoard(card.id)){
                disabledCards.add(card);
                availableCards.remove(card);
            }
            switch(card.id){
                case MISS:
                    if(currentPlayer.figure.id!=fig_id.CALAMITY_JANET || !currentPlayer.canBang((int)Math.floor((double) playersList.size()/2)) || bangUsed) {
                        disabledCards.add(card);
                        availableCards.remove(card);
                    }
                    break;
                case BANG:
                    if(!currentPlayer.canBang((int)Math.floor((double) playersList.size()/2)) || bangUsed){
                        disabledCards.add(card);
                        availableCards.remove(card);
                    }
                    break;
                case ROBBERY:
                    if(!currentPlayer.canSteal()){
                        disabledCards.add(card);
                        availableCards.remove(card);
                    }
                    break;
                case JAIL:
                    if(!currentPlayer.canJail()){
                        disabledCards.add(card);
                        availableCards.remove(card);
                    }
                    break;
                case LOVESTRIKE:
                    if(!currentPlayer.canCoupDeFoudre()){
                        disabledCards.add(card);
                        availableCards.remove(card);
                    }
                    break;
            }
        }
        phase2Moves.add(new PlayMove(availableCards, disabledCards));
        if(currentPlayer.figure.id == fig_id.SID_KETCHUM && currentPlayer.handCards.size()>=2){
            phase2Moves.add(new PickCardMove(currentPlayer.handCards,2, PickCardMove.PickType.HEALTHROW));
        }
        phase2Moves.add(new PassMove(PassMove.PassReason.ENDTURN));

        interactionStack.addFirst(new Action(currentPlayer, phase2Moves));
        interactionStack.addFirst(phase1Info);
    }


    public void playProcess(Player player, PlayMove move){
        Info info = null;
        Info infoCardPlayed = null;
        ArrayList<Player> targetList = null ;
        Player nextPlayer = null;
        ArrayList<Move> moveList = null;
        cardPlayed = null;
        if(move.playedCard.type == Card.Card_type.WEAPON) {
            Card existingWeapon = player.hasWeaponOnBoard();
            if (existingWeapon != null) {
                throwDeque.push(player.removeBoardCard(existingWeapon));
            }
            player.addBoardCard(move.playedCard);
        }else if (move.playedCard.type == Card.Card_type.ABILITY && move.playedCard.id != Card_id.JAIL){
            player.addBoardCard(move.playedCard);
        }else if(move.playedCard.id == Card_id.JAIL) {
            targetList = new ArrayList<>();
            nextPlayer = player.nextPlayer;
            while(nextPlayer != player){
                if(nextPlayer.role != Role.SHERIF && !nextPlayer.hasCardOnBoard(Card_id.JAIL)) targetList.add(nextPlayer);
                nextPlayer = nextPlayer.nextPlayer;
            }
            moveList = new ArrayList<>();
            moveList.add(new TargetMove(targetList, TargetMove.TargetType.JAIL));
            interactionStack.addFirst(new Action(player, moveList));
        }else{
            switch(move.playedCard.id){
                case BANG:
                    moveList = new ArrayList<>();
                    moveList.add(new TargetMove(player.getAvailableTarget(player.vision + player.weaponVision, (int) Math.floor((double)playersList.size()/2)), TargetMove.TargetType.BANG));
                    interactionStack.add(new Action(player,moveList));
                    infoCardPlayed = new Info(player, Info.InfoType.CARDBANG);
                    interactionStack.addFirst(infoCardPlayed);
                    if(player.figure.id != fig_id.WILLY_THE_KID || !player.hasCardOnBoard(Card_id.VOLCANIC)){
                        bangUsed = true;
                    }
                    cardPlayed=Card_id.BANG;
                    break;
                case BEER:
                    if(playersList.size() > 2 && player.healthPoint < player.maxHealthPoint){
                        info = new Info(player, Info.InfoType.BEERHEAL);
                        interactionStack.addFirst(info);
                        player.healthPoint++;
                    }
                    infoCardPlayed = new Info(player, Info.InfoType.CARDBEER);
                    interactionStack.addFirst(infoCardPlayed);
                    break;
                case MISS:
                    moveList = new ArrayList<>();
                    moveList.add(new TargetMove(player.getAvailableTarget(player.vision + player.weaponVision, (int) Math.floor((double)playersList.size()/2)), TargetMove.TargetType.BANG));
                    interactionStack.add(new Action(player,moveList));
                    infoCardPlayed = new Info(player, Info.InfoType.CARDRATE);
                    interactionStack.addFirst(infoCardPlayed);
                    if(player.figure.id != fig_id.WILLY_THE_KID && !player.hasCardOnBoard(Card_id.VOLCANIC)){
                        bangUsed = true;
                    }
                    cardPlayed=Card_id.MISS;
                    break;
                case SALOON:
                    for(Player saloonPlayer : playersList.values()){
                        if(saloonPlayer.healthPoint < saloonPlayer.maxHealthPoint){
                            info = new Info(saloonPlayer, Info.InfoType.SALOONHEAL);
                            interactionStack.addFirst(info);
                            saloonPlayer.healthPoint++;
                        }
                    }
                    infoCardPlayed = new Info(player, Info.InfoType.CARDSALOON);
                    interactionStack.addFirst(infoCardPlayed);
                    break;
                case SHOP:
                    ArrayList<Card> cards = new ArrayList<>();
                    for(int i = 0; i < playersList.size(); i++){
                        cards.add(cardDeque.pop());
                    }
                    moveList = new ArrayList<>();
                    moveList.add(new PickCardMove(cards,1, PickCardMove.PickType.SHOP));
                    interactionStack.addFirst(new Action(player, moveList));
                    infoCardPlayed = new Info(player, Info.InfoType.CARDSHOP);
                    interactionStack.addFirst(infoCardPlayed);
                    break;
                case APACHE:
                    Player target = player.prevPlayer;
                    while(target != player){
                        cards = new ArrayList<>();
                        for(Card c : target.handCards){
                            if(c.id == Card_id.BANG || (c.id == Card_id.MISS && target.figure.id == fig_id.CALAMITY_JANET)){
                                cards.add(c);
                            }
                        }
                        moveList = new ArrayList<>();
                        moveList.add(new PickCardMove(cards,1, PickCardMove.PickType.APACHE));
                        moveList.add(new PassMove(PassMove.PassReason.DEFAPACHEPASS));
                        interactionStack.add(new Action(target,moveList));
                        target = target.prevPlayer;
                    }
                    infoCardPlayed = new Info(player, Info.InfoType.CARDAPACHE);
                    interactionStack.addFirst(infoCardPlayed);
                    cardPlayed=Card_id.APACHE;
                    break;
                case GATLING:
                    target = player.prevPlayer;
                    while(target != player){
                        cards = new ArrayList<>();
                        for(Card c : target.handCards){
                            if(c.id == Card_id.MISS || (c.id == Card_id.BANG && target.figure.id == fig_id.CALAMITY_JANET)){
                                cards.add(c);
                            }
                        }
                        moveList = new ArrayList<>();
                        moveList.add(new PickCardMove(cards,1, PickCardMove.PickType.GATLING));
                        moveList.add(new PassMove(PassMove.PassReason.DEFGATLINGPASS));
                        interactionStack.add(new Action(target,moveList));
                        target = target.prevPlayer;
                    }
                    infoCardPlayed = new Info(player, Info.InfoType.CARDGATLING);
                    interactionStack.addFirst(infoCardPlayed);
                    cardPlayed=Card_id.GATLING;
                    break;
                case CONVOY:
                    moveList = new ArrayList<>();
                    cards = new ArrayList<>();
                    cards.add(cardDeque.pop());
                    cards.add(cardDeque.pop());
                    moveList.add(new GetCardMove(cards));
                    interactionStack.addFirst(new Action(player, moveList));
                    infoCardPlayed =  new Info(player, Info.InfoType.CARDCONVOI);
                    interactionStack.addFirst(infoCardPlayed);
                    break;
                case DILIGENCE:
                    moveList = new ArrayList<>();
                    cards = new ArrayList<>();
                    cards.add(cardDeque.pop());
                    cards.add(cardDeque.pop());
                    moveList.add(new GetCardMove(cards));
                    interactionStack.addFirst(new Action(player, moveList));
                    infoCardPlayed =  new Info(player, Info.InfoType.CARDDILIGENCE);
                    interactionStack.addFirst(infoCardPlayed);
                    break;
                case ROBBERY:
                    targetList = player.getAvailableTarget(player.vision, (int) Math.floor((double) playersList.size() / 2));
                    moveList = new ArrayList<>();
                    moveList.add(new TargetMove(targetList, TargetMove.TargetType.ROBBERY));
                    interactionStack.addFirst(new Action(player, moveList));
                    infoCardPlayed =  new Info(player, Info.InfoType.CARDROBBERY);
                    interactionStack.addFirst(infoCardPlayed);
                    break;
                case LOVESTRIKE:
                    targetList = player.getAllOtherTarget();
                    moveList = new ArrayList<>();
                    moveList.add(new TargetMove(targetList, TargetMove.TargetType.LOVESTRIKE));
                    interactionStack.addFirst(new Action(player, moveList));
                    infoCardPlayed =  new Info(player, Info.InfoType.CARDLOVESTRIKE);
                    interactionStack.addFirst(infoCardPlayed);
                    break;
                case DUEL:
                    targetList = player.getAllOtherTarget();
                    moveList = new ArrayList<>();
                    moveList.add(new TargetMove(targetList, TargetMove.TargetType.DUEL));
                    interactionStack.addFirst(new Action(player, moveList));
                    infoCardPlayed =  new Info(player, Info.InfoType.CARDDUEL);
                    interactionStack.addFirst(infoCardPlayed);
                    cardPlayed=Card_id.DUEL;
                    break;
            }
            throwDeque.push(player.removeHandCard(move.playedCard.id));
        }
    }

    public void moveProcess(Player player, Move chosenMove){
        if(currentCard != null){
            currentCard.action(player, chosenMove, game);
        }


        switch(chosenMove.type){
            case PICKCARD:
                pickMoveProcess(player, (PickCardMove) chosenMove);
                break;
            case CHOICE:
                choiceMoveProcess(player, (ChoiceMove) chosenMove);
                break;
            case PASS:
                passMoveProcess(player, (PassMove) chosenMove);
                break;
            case TARGET:
                targetMoveProcess(player, (TargetMove) chosenMove);
                break;
            case GETCARD:
                getCardMoveProcess(player, (GetCardMove) chosenMove);
                break;
            case PLAYCARD:
                playProcess(player,(PlayMove) chosenMove);
        }
    }

    public void targetMoveProcess(Player player, TargetMove move){
        ArrayList<Move> moveList;
        switch(move.targetType){
            case STEAL:
                player.handCards.add(move.selectedPlayer.removeRandomHandCard());
                player.handCards.add(cardDeque.pop());
                interactionStack.addFirst(new Info(player, Info.InfoType.PHASE1STEAL, move.selectedPlayer));
                break;
            case JAIL:
                move.selectedPlayer.addBoardCard(player.removeHandCard(Card_id.JAIL));
                interactionStack.addFirst(new Info(player, Info.InfoType.CARDJAIL, move.selectedPlayer));
                break;
            case LOVESTRIKE:
                if(move.selectedPlayer.boardCards.size() == 0){
                    move.selectedPlayer.removeRandomHandCard();
                    interactionStack.addFirst(new Info(player, Info.InfoType.LOVESTRIKEHAND, move.selectedPlayer));
                }else if (move.selectedPlayer.handCards.size() == 0){
                    moveList = new ArrayList<>();
                    moveList.add(new PickCardMove(move.selectedPlayer.boardCards, 1, PickCardMove.PickType.LOVESTRIKE));
                    interactionStack.addFirst(new Action(player, moveList));
                    interactionStack.addFirst(new Info(player, Info.InfoType.LOVESTRIKEBOARD, move.selectedPlayer));
                    targets.put(TargetMove.TargetType.LOVESTRIKE, move.selectedPlayer);
                }else{
                    moveList = new ArrayList<>();
                    moveList.add(new ChoiceMove(ChoiceMove.Choice.LOVESTRIKE));
                    interactionStack.addFirst(new Action(player, moveList));
                    targets.put(TargetMove.TargetType.LOVESTRIKE, move.selectedPlayer);
                }
                break;
            case ROBBERY:
                if(move.selectedPlayer.boardCards.size() == 0){
                    player.handCards.add(move.selectedPlayer.removeRandomHandCard());
                    interactionStack.addFirst(new Info(player, Info.InfoType.ROBBERYHAND));
                }else if (move.selectedPlayer.handCards.size() == 0){
                    moveList = new ArrayList<>();
                    moveList.add(new PickCardMove(move.selectedPlayer.boardCards, 1, PickCardMove.PickType.ROBBERY));
                    interactionStack.addFirst(new Action(player, moveList));
                    targets.put(TargetMove.TargetType.ROBBERY, move.selectedPlayer);
                }else{
                    moveList = new ArrayList<>();
                    moveList.add(new ChoiceMove(ChoiceMove.Choice.ROBBERY));
                    interactionStack.addFirst(new Action(player, moveList));
                    targets.put(TargetMove.TargetType.ROBBERY, move.selectedPlayer);
                }
                break;
            case DUEL:
                moveList = new ArrayList<>();
                ArrayList<Card> cards = new ArrayList<>();
                for(Card card : move.selectedPlayer.handCards){
                    if(card.id == Card_id.BANG || (card.id == Card_id.MISS && move.selectedPlayer.figure.id == fig_id.CALAMITY_JANET)){
                        cards.add(card);
                    }
                }
                moveList.add(new PickCardMove(cards, 1 ,PickCardMove.PickType.DUEL));
                moveList.add(new PassMove(PassMove.PassReason.PASSDUEL));
                interactionStack.addFirst(new Action(move.selectedPlayer, moveList));
                interactionStack.addFirst(new Info(player, Info.InfoType.DUEL, move.selectedPlayer));
                targets.put(TargetMove.TargetType.DUEL, player);
                break;
            case BANG:
                int defenceNeeded;
                boolean jourdonnais = false;
                boolean planque = false;
                boolean missJourdonnais = false;
                boolean missPlanque = false;
                boolean slab = false;
                if(player.figure.id == fig_id.SLAB_THE_KILLER){
                    slab = true;
                    defenceNeeded = 2;
                }else{
                    defenceNeeded = 1;
                }

                if(move.selectedPlayer.figure.id == fig_id.JOURDONNAIS){
                    jourdonnais = true;
                    if(quickDraw(4)){
                        defenceNeeded--;
                        missJourdonnais =  true;
                    }
                }

                if(defenceNeeded > 0 && move.selectedPlayer.hasBoardCard(Card_id.HIDEOUT)){
                    planque = true;
                    if(quickDraw(4)){
                        defenceNeeded--;
                        missPlanque =  true;
                    }
                }

                if(defenceNeeded <= 0){
                    interactionStack.addFirst(new Info(player, Info.InfoType.BANGTARGETFAIL , move.selectedPlayer));
                }else {
                    moveList = new ArrayList<>();
                    cards = new ArrayList<>();
                    for (Card card : move.selectedPlayer.handCards) {
                        if (card.id == Card_id.MISS || (card.id == Card_id.BANG && move.selectedPlayer.figure.id == fig_id.CALAMITY_JANET)) {
                            cards.add(card);
                        }
                    }
                    if (cards.size() >= defenceNeeded) {
                        moveList.add(new PickCardMove(cards, defenceNeeded, PickCardMove.PickType.BANG));
                    }
                    moveList.add(new PassMove(PassMove.PassReason.BANGPASS));
                    interactionStack.addFirst(new Action(move.selectedPlayer, moveList));
                    interactionStack.addFirst(new Info(move.selectedPlayer, Info.InfoType.BANGDEFENCE));
                    if (slab && (missPlanque || missJourdonnais)){
                        interactionStack.addFirst(new Info(move.selectedPlayer, Info.InfoType.BANGDEFENCEINEFFICIENT));
                    }
                }
                if(jourdonnais){
                    if(missJourdonnais){
                        interactionStack.addFirst(new Info(move.selectedPlayer, Info.InfoType.JOURDONNAISQUICKDRAW));
                    }else{
                        interactionStack.addFirst(new Info(move.selectedPlayer, Info.InfoType.JOURDONNAISMISSFAIL));
                    }
                }
                if(planque){
                    if(missPlanque){
                        interactionStack.addFirst(new Info(move.selectedPlayer, Info.InfoType.PLANQUEMISSWIN));
                    }else{
                        interactionStack.addFirst(new Info(move.selectedPlayer, Info.InfoType.HIDEOUTQUICKDRAW));
                    }
                }
                if(slab){
                    interactionStack.addFirst(new Info(move.selectedPlayer, Info.InfoType.SLABBANG));
                }
                interactionStack.addFirst(new Info(player, Info.InfoType.BANGTARGET , move.selectedPlayer));
                break;
        }
    }

    public void pickMoveProcess(Player player, PickCardMove move){
        ArrayList<Move> moveList = new ArrayList<>();
        switch(move.pickType){
            case PHASE1CHOOSE:
                moveList.add(new GetCardMove(move.chosenCards));
                for(Card c : move.cardsToGet){
                    if(!move.chosenCards.contains(c)) cardDeque.push(c);
                }
                interactionStack.addFirst(new Action(player,moveList));
                interactionStack.addFirst(new Info(player, Info.InfoType.PHASE1CHOOSE));
                break;
            case THROW:
                for(Card card : move.chosenCards){
                    throwDeque.push(card);
                    player.handCards.remove(card);
                }
                state = State.END;
                break;
            case ROBBERY:
                for(Card c : move.chosenCards) {
                    player.handCards.add(targets.get(TargetMove.TargetType.ROBBERY).removeBoardCard(c));
                    interactionStack.addFirst(new Info(player, Info.InfoType.ROBBERYBOARD, c));
                }
                targets.remove(TargetMove.TargetType.ROBBERY);
                break;
            case LOVESTRIKE:
                Card stealedCard = null;
                for(Card c : move.chosenCards){
                    throwDeque.push(targets.get(TargetMove.TargetType.LOVESTRIKE).removeBoardCard(c));
                    stealedCard = c;
                }
                targets.remove(TargetMove.TargetType.LOVESTRIKE);
                interactionStack.addFirst(new Info(player, Info.InfoType.LOVESTRIKEBOARD, stealedCard));
                break;
            case DUEL:
                Card usedCard = null;
                for(Card c : move.chosenCards){
                    throwDeque.push(c);
                    usedCard = c;
                    player.handCards.remove(c);
                }
                moveList = new ArrayList<>();
                ArrayList<Card> cards = new ArrayList<>();
                Player opponent = targets.put(TargetMove.TargetType.DUEL,player);
                for(Card card : opponent.handCards){
                    if(card.id == Card_id.BANG || (card.id == Card_id.MISS && opponent.figure.id == fig_id.CALAMITY_JANET)){
                        cards.add(card);
                    }
                }
                moveList.add(new PickCardMove(cards, 1 ,PickCardMove.PickType.DUEL));
                moveList.add(new PassMove(PassMove.PassReason.PASSDUEL));
                interactionStack.addFirst(new Action(opponent, moveList));
                interactionStack.addFirst(new Info(opponent, Info.InfoType.DEFDUELSUCCES,usedCard));
                break;
            case SHOP:
                Card chosenCard = move.chosenCards.get(0);
                player.handCards.add(chosenCard);
                move.cardsToGet.remove(chosenCard);

                if(move.cardsToGet.size() > 0) {
                    moveList = new ArrayList<>();
                    moveList.add(new PickCardMove(move.cardsToGet, 1, PickCardMove.PickType.SHOP));
                    interactionStack.addFirst(new Action(player.nextPlayer, moveList));
                }

                interactionStack.addFirst(new Info(player, Info.InfoType.SHOPPICKED, chosenCard));
                break;
            case APACHE:
                for(Card card : move.chosenCards){
                    throwDeque.push(card);
                    player.handCards.remove(card);
                    interactionStack.addFirst(new Info(player, Info.InfoType.DEFAPACHESUCCESS, card));
                }
                break;
            case GATLING:
                for(Card card : move.chosenCards){
                    throwDeque.push(card);
                    player.handCards.remove(card);
                    interactionStack.addFirst(new Info(player, Info.InfoType.DEFGATLINGSUCCESS, card));
                }
                break;
            case BANG:
                for(Card card : move.chosenCards){
                    throwDeque.push(card);
                    player.handCards.remove(card);
                    interactionStack.addFirst(new Info(player, Info.InfoType.DEFBANG, card));
                }
                break;
        }
    }

    public void choiceMoveProcess(Player player,ChoiceMove move){
        ArrayList<Move> moveList = new ArrayList<>();

        ArrayList<Card> cards = new ArrayList<>();
        Info info=null;
        switch(move.choice){
            case PICKTRASH:
                if(move.selectedAnswer == ChoiceMove.Answer.YES){
                    cards.add(throwDeque.pop());
                    cards.add(cardDeque.pop());
                    info =  new Info(player, Info.InfoType.PHASE1TRASH);
                }else{
                    cards.add(cardDeque.pop());
                    cards.add(cardDeque.pop());
                    info =  new Info(player, Info.InfoType.PHASE1);
                }
                moveList.add(new GetCardMove(cards));
                break;
            case PICKPLAYER:
                if(move.selectedAnswer == ChoiceMove.Answer.YES){
                    ArrayList<Player> playerList = new ArrayList<>(playersList.values());
                    playerList.remove(player);
                    moveList.add(new TargetMove(playerList, TargetMove.TargetType.STEAL));
                }else{
                    cards.add(cardDeque.pop());
                    cards.add(cardDeque.pop());
                    moveList.add(new GetCardMove(cards));
                    info =  new Info(player, Info.InfoType.PHASE1);
                }
                break;
            case LOVESTRIKE:
                if(move.selectedAnswer == ChoiceMove.Answer.HAND){
                    targets.get(TargetMove.TargetType.LOVESTRIKE).removeRandomHandCard();
                    info =  new Info(player, Info.InfoType.LOVESTRIKEHAND, targets.get(TargetMove.TargetType.LOVESTRIKE));
                    targets.remove(TargetMove.TargetType.LOVESTRIKE);
                }else{
                    moveList = new ArrayList<>();
                    moveList.add(new PickCardMove(targets.get(TargetMove.TargetType.LOVESTRIKE).boardCards, 1, PickCardMove.PickType.LOVESTRIKE));
                    info = new Info(player, Info.InfoType.LOVESTRIKEBOARD, targets.get(TargetMove.TargetType.LOVESTRIKE));
                }
            case ROBBERY:
                if(move.selectedAnswer == ChoiceMove.Answer.HAND){
                    player.handCards.add(targets.get(TargetMove.TargetType.ROBBERY).removeRandomHandCard());
                    info =  new Info(player, Info.InfoType.ROBBERYHAND, targets.get(TargetMove.TargetType.ROBBERY));
                    targets.remove(TargetMove.TargetType.ROBBERY);
                }else{
                    moveList = new ArrayList<>();
                    moveList.add(new PickCardMove(targets.get(TargetMove.TargetType.ROBBERY).boardCards, 1, PickCardMove.PickType.ROBBERY));
                    info = new Info(player, Info.InfoType.ROBBERYBOARD, targets.get(TargetMove.TargetType.ROBBERY));
                }

        }
        interactionStack.addFirst(new Action(player,moveList));
        interactionStack.addFirst(info);
    }

    public void passMoveProcess(Player player, PassMove passMove){
        switch(passMove.reason){
            case ENDTURN:
                if(player.handCards.size() > player.healthPoint){
                    state = State.TRASH;
                    ArrayList<Move> moveList = new ArrayList<>();
                    moveList.add(new PickCardMove(player.handCards, player.handCards.size()-player.healthPoint ,PickCardMove.PickType.THROW));
                    interactionStack.addFirst(new Action(player, moveList));
                    interactionStack.addFirst(new Info(player, Info.InfoType.THROW));
                }else{
                    state = State.END;
                    interactionStack.addFirst(new Info(player, Info.InfoType.END));
                }
                break;
            case PASSDUEL:
                player.healthPoint--;
                if(checkMort(player)){
                    stealFromElGringo(player,targets.get(TargetMove.TargetType.DUEL));
                    drawBartCassidy(player, 1);
                }
                targets.remove(TargetMove.TargetType.DUEL);
                interactionStack.addFirst(new Info(player, Info.InfoType.PASSDUEL));
                break;
            case DEFAPACHEPASS:
                player.healthPoint--;
                if(checkMort(player)){
                    stealFromElGringo(player,currentPlayer);
                    drawBartCassidy(player, 1);
                }
                interactionStack.addFirst(new Info(player, Info.InfoType.DEFAPACHEFAIL));
                break;
            case DEFGATLINGPASS:
                player.healthPoint--;
                if(checkMort(player)){
                    stealFromElGringo(player,currentPlayer);
                    drawBartCassidy(player, 1);
                }
                interactionStack.addFirst(new Info(player, Info.InfoType.DEFGATLINGFAIL));
                break;
            case BANGPASS:
                player.healthPoint--;
                if(checkMort(player)){
                    stealFromElGringo(player,currentPlayer);
                    drawBartCassidy(player, 1);
                }
                interactionStack.addFirst(new Info(player, Info.InfoType.ENDBANG));
                break;
            case ENDLIFE:
                boolean deputy = false;
                boolean renegate = false;
                boolean outlaw = false;
                boolean sherif = false;

                for (Player p : playersList.values()){
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
                    if(cardPlayed == Card_id.DUEL || cardPlayed == Card_id.BANG || cardPlayed == Card_id.MISS || cardPlayed == Card_id.APACHE || cardPlayed == Card_id.GATLING){
                        if(player != currentPlayer && player.role == Role.DEPUTY && currentPlayer.role == Role.SHERIF){
                            ArrayList<Move> moveList = new ArrayList<>();
                            moveList.add(new PickCardMove(currentPlayer.handCards, currentPlayer.handCards.size(), PickCardMove.PickType.THROWSHERIF ));
                            interactionStack.addFirst(new Action(currentPlayer, moveList));
                            interactionStack.addFirst(new Info(currentPlayer, Info.InfoType.SHERIFKILLDEPUTY, player));
                        }else if(player.role == Role.OUTLAW){
                            ArrayList<Move> moveList = new ArrayList<>();
                            ArrayList<Card> cardsToGet = new ArrayList<>();
                            cardsToGet.add(cardDeque.pop());
                            cardsToGet.add(cardDeque.pop());
                            cardsToGet.add(cardDeque.pop());
                            moveList.add(new GetCardMove(cardsToGet));
                            interactionStack.addFirst(new Action(currentPlayer, moveList));
                            interactionStack.addFirst(new Info(currentPlayer, Info.InfoType.OUTLAWKILLED, player));
                        }
                    }
                    vultureAction(player);
                }
                interactionStack.addFirst(new Info(player, Info.InfoType.DEAD));
        }
    }

    public void getCardMoveProcess(Player player, GetCardMove chosenMove){
        for(Card card : chosenMove.cardToGet){
            player.handCards.add(card);
        }
    }











    public void vultureAction(Player victim){
        ArrayList<Card> cardsToGet = new ArrayList<>();

        for(Player player : playersList.values()){
            if(player != victim && player.figure.id == fig_id.VULTURE_SAM){
                cardsToGet.addAll(victim.handCards);
                cardsToGet.addAll(victim.boardCards);

                ArrayList<Move> moveList = new ArrayList<>();
                moveList.add(new GetCardMove(cardsToGet));
                interactionStack.addFirst(new Action(player, moveList));
                interactionStack.addFirst(new Info(player, Info.InfoType.VULTURE, victim));
                break;
            }
        }
    }
}
