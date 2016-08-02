package com.example.olivo.bangmobile.gameMechanics.elements;

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
import com.example.olivo.bangmobile.gameMechanics.elements.Card.Card_id;
import com.example.olivo.bangmobile.gameMechanics.elements.Figure.fig_id;

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
    public Deque<Card> cardDeque;
    public Deque<Card> throwDeque;
    Map<TargetMove.TargetType, Player> targets = new HashMap<>();


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
        PHASE1,
        PHASE2,
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
                if(!checkMort(currentPlayer)){
                    drawBartCassidy(currentPlayer, 3);
                }
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
            if(quickDraw(4)) {
                jail = currentPlayer.figure.id != fig_id.LUCKY_DUKE || !quickDraw(4);
            }
            currentPlayer.removeBoardCard(Card_id.PRISON);
            if(!jail){
                interactionStack.addLast(new Info(currentPlayer, Info.InfoType.JAIL_EVADE));
                state=State.PHASE1;
            }else{
                interactionStack.addLast(new Info(currentPlayer, Info.InfoType.JAIL_STAY));
                state=State.END;
            }
        }
    }

    public void getPhase1(){
        Info phase1Info=null;
        ArrayList<Move> phase1Moves=new ArrayList<>();
        ArrayList<Card> cards = new ArrayList<>();
        switch(currentPlayer.figure.id){
            case BLACK_JACK:
                if(quickDraw(4)){
                    phase1Info = new Info(currentPlayer, Info.InfoType.PHASE1BONUS);
                    cards.add(cardDeque.pop());
                    cards.add(cardDeque.pop());
                    cards.add(cardDeque.pop());

                }else{
                    phase1Info = new Info(currentPlayer, Info.InfoType.PHASE1);
                    cards.add(cardDeque.pop());
                    cards.add(cardDeque.pop());
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
        interactionStack.addFirst(new Action(currentPlayer,phase1Moves));
        interactionStack.addFirst(phase1Info);
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
                case RATE:
                    if(currentPlayer.figure.id!=fig_id.CALAMITY_JANET || !currentPlayer.canBang((int)Math.floor((double) playersList.size()/2))) {
                        disabledCards.add(card);
                        availableCards.remove(card);
                    }
                    break;
                case BANG:
                    if(!currentPlayer.canBang((int)Math.floor((double) playersList.size()/2))){
                        disabledCards.add(card);
                        availableCards.remove(card);
                    }
                    break;
                case BRAQUAGE:
                    if(!currentPlayer.canSteal()){
                        disabledCards.add(card);
                        availableCards.remove(card);
                    }
                    break;
                case PRISON:
                    if(!currentPlayer.canJail()){
                        disabledCards.add(card);
                        availableCards.remove(card);
                    }
                    break;
                case COUPDEFOUDRE:
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



    private boolean checkMort(Player player) {
        if(player.healthPoint <= 0){
            interactionStack.addLast(new Info(player, Info.InfoType.DYING));
            ArrayList<Move> movesList = new ArrayList<>();
            if(playersList.size()>2){
                if(player.hasAmountOfCardInHand(Card_id.BIERE,(player.healthPoint*-1+1))){
                    movesList.add(new ChoiceMove(ChoiceMove.Choice.SAVEBEER));
                }
            }
            if(player.handCards.size()>=(player.healthPoint*-1+1)*2 && player.figure.id == fig_id.SID_KETCHUM){
                movesList.add(new PickCardMove(player.handCards,(player.healthPoint*-1+1)*2, PickCardMove.PickType.HEALTHROW));
            }
            movesList.add(new PassMove(PassMove.PassReason.ENDLIFE));
            interactionStack.addFirst(new Action(player,movesList));
            return true;
        }
        return false;
    }

    public static boolean quickDraw(int max){
        Random rand = new Random();
        return (rand.nextInt(max)==(max-1));
    }


    public void playProcess(Player player, PlayMove move){
        Info info = null;
        ArrayList<Player> targetList = null ;
        Player nextPlayer = null;
        ArrayList<Move> moveList = null;
        if(move.playedCard.type == Card.Card_type.WEAPON) {
            Card existingWeapon = player.hasWeaponOnBoard();
            if (existingWeapon != null) {
                throwDeque.push(player.removeBoardCard(existingWeapon.id));
            }
            player.addBoardCard(move.playedCard);
        }else if (move.playedCard.type == Card.Card_type.ABILITY && move.playedCard.id != Card_id.PRISON){
            player.addBoardCard(move.playedCard);
        }else if(move.playedCard.id == Card_id.PRISON) {
            targetList = new ArrayList<>();
            nextPlayer = player.nextPlayer;
            while(nextPlayer != player){
                if(nextPlayer.role != Role.SHERIF && !nextPlayer.hasCardOnBoard(Card_id.PRISON)) targetList.add(nextPlayer);
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
                    info = new Info(player, Info.InfoType.CARDBANG);
                    interactionStack.addFirst(info);
                    break;
                case BIERE:
                    if(playersList.size() > 2 && player.healthPoint < player.maxHealthPoint){
                        info = new Info(player, Info.InfoType.BIEREHEAL);
                        interactionStack.addFirst(info);
                        player.healthPoint++;
                    }
                    info = new Info(player, Info.InfoType.CARDBIERE);
                    interactionStack.addFirst(info);
                    break;
                case RATE:
                    moveList = new ArrayList<>();
                    moveList.add(new TargetMove(player.getAvailableTarget(player.vision + player.weaponVision, (int) Math.floor((double)playersList.size()/2)), TargetMove.TargetType.BANG));
                    interactionStack.add(new Action(player,moveList));
                    info = new Info(player, Info.InfoType.CARDBANG);
                    interactionStack.addFirst(info);
                    break;
                case SALOON:
                    for(Player saloonPlayer : playersList.values()){
                        if(saloonPlayer.healthPoint < saloonPlayer.maxHealthPoint){
                            info = new Info(saloonPlayer, Info.InfoType.SALOONHEAL);
                            interactionStack.addFirst(info);
                            saloonPlayer.healthPoint++;
                        }
                    }
                    info = new Info(player, Info.InfoType.CARDSALOON);
                    interactionStack.addFirst(info);
                    break;
                case MAGASIN:
                    ArrayList<Card> cards = new ArrayList<>();
                    for(int i = 0; i < playersList.size(); i++){
                        cards.add(cardDeque.pop());
                    }
                    moveList = new ArrayList<>();
                    moveList.add(new PickCardMove(cards,1, PickCardMove.PickType.MAGASIN));
                    interactionStack.addFirst(new Action(player, moveList));
                    info = new Info(player, Info.InfoType.CARDMAGASIN);
                    interactionStack.addFirst(info);
                    break;
                case INDIENS:
                    Player target = player.prevPlayer;
                    while(target != player){
                        cards = new ArrayList<>();
                        for(Card c : target.handCards){
                            if(c.id == Card_id.BANG || (c.id == Card_id.RATE && target.figure.id == fig_id.CALAMITY_JANET)){
                                cards.add(c);
                            }
                        }
                        moveList = new ArrayList<>();
                        moveList.add(new PickCardMove(cards,1, PickCardMove.PickType.INDIENS));
                        moveList.add(new PassMove(PassMove.PassReason.FAILINDIENS));
                        interactionStack.add(new Action(target,moveList));
                        target = target.prevPlayer;
                    }
                    info = new Info(player, Info.InfoType.CARDINDIENS);
                    interactionStack.addFirst(info);
                    break;
                case GATLING:
                    target = player.prevPlayer;
                    while(target != player){
                        cards = new ArrayList<>();
                        for(Card c : target.handCards){
                            if(c.id == Card_id.RATE || (c.id == Card_id.BANG && target.figure.id == fig_id.CALAMITY_JANET)){
                                cards.add(c);
                            }
                        }
                        moveList = new ArrayList<>();
                        moveList.add(new PickCardMove(cards,1, PickCardMove.PickType.GATLING));
                        moveList.add(new PassMove(PassMove.PassReason.FAILGATLING));
                        interactionStack.add(new Action(target,moveList));
                        target = target.prevPlayer;
                    }
                    info = new Info(player, Info.InfoType.CARDGATLING);
                    interactionStack.addFirst(info);
                    break;
                case CONVOI:
                    moveList = new ArrayList<>();
                    cards = new ArrayList<>();
                    cards.add(cardDeque.pop());
                    cards.add(cardDeque.pop());
                    moveList.add(new GetCardMove(cards));
                    interactionStack.addFirst(new Action(player, moveList));
                    info =  new Info(player, Info.InfoType.CARDCONVOI);
                    interactionStack.addFirst(info);
                    break;
                case DILIGENCE:
                    moveList = new ArrayList<>();
                    cards = new ArrayList<>();
                    cards.add(cardDeque.pop());
                    cards.add(cardDeque.pop());
                    moveList.add(new GetCardMove(cards));
                    interactionStack.addFirst(new Action(player, moveList));
                    info =  new Info(player, Info.InfoType.CARDDILIGENCE);
                    interactionStack.addFirst(info);
                    break;
                case BRAQUAGE:
                    targetList = player.getAvailableTarget(player.vision, (int) Math.floor((double) playersList.size() / 2));
                    moveList = new ArrayList<>();
                    moveList.add(new TargetMove(targetList, TargetMove.TargetType.BRAQUAGE));
                    interactionStack.addFirst(new Action(player, moveList));
                    info =  new Info(player, Info.InfoType.CARDBRAQUAGE);
                    interactionStack.addFirst(info);
                    break;
                case COUPDEFOUDRE:
                    targetList = player.getAllOtherTarget();
                    moveList = new ArrayList<>();
                    moveList.add(new TargetMove(targetList, TargetMove.TargetType.COUPDEFOUDRE));
                    interactionStack.addFirst(new Action(player, moveList));
                    info =  new Info(player, Info.InfoType.CARDCOUPDEFOUDRE);
                    interactionStack.addFirst(info);
                    break;
                case DUEL:
                    targetList = player.getAllOtherTarget();
                    moveList = new ArrayList<>();
                    moveList.add(new TargetMove(targetList, TargetMove.TargetType.DUEL));
                    interactionStack.addFirst(new Action(player, moveList));
                    info =  new Info(player, Info.InfoType.CARDDUEL);
                    interactionStack.addFirst(info);
                    break;
            }
            throwDeque.push(player.removeHandCard(move.playedCard.id));
        }
    }


    public void moveProcess(Player player, Move chosenMove){
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
        Info info=null;
        ArrayList<Move> moveList;
        switch(move.targetType){
            case STEAL:
                player.handCards.add(move.selectedPlayer.removeRandomHandCard());
                player.handCards.add(cardDeque.pop());
                info =  new Info(player, Info.InfoType.PHASE1STEAL, move.selectedPlayer);
                break;
            case JAIL:
                move.selectedPlayer.addBoardCard(player.removeHandCard(Card_id.PRISON));
                info =  new Info(player, Info.InfoType.CARDPRISON, move.selectedPlayer);
                checkSuziLafayette();
                break;
            case COUPDEFOUDRE:
                if(move.selectedPlayer.boardCards.size() == 0){
                    move.selectedPlayer.removeRandomHandCard();
                    info =  new Info(player, Info.InfoType.COUPDEFOUDREHAND, move.selectedPlayer);
                    checkSuziLafayette();
                    checkSuziLafayette();
                }else if (move.selectedPlayer.handCards.size() == 0){
                    moveList = new ArrayList<>();
                    moveList.add(new PickCardMove(move.selectedPlayer.boardCards, 1, PickCardMove.PickType.COUPDEFOUDRE));
                    interactionStack.addFirst(new Action(player, moveList));
                    info = new Info(player, Info.InfoType.COUPDEFOUDREBOARD, move.selectedPlayer);
                    interactionStack.addFirst(info);
                    targets.put(TargetMove.TargetType.COUPDEFOUDRE, move.selectedPlayer);
                }else{
                    moveList = new ArrayList<>();
                    moveList.add(new ChoiceMove(ChoiceMove.Choice.COUPDEFOUDRE));
                    interactionStack.addFirst(new Action(player, moveList));
                    targets.put(TargetMove.TargetType.COUPDEFOUDRE, move.selectedPlayer);
                }
                break;
            case BRAQUAGE:
                if(move.selectedPlayer.boardCards.size() == 0){
                    player.handCards.add(move.selectedPlayer.removeRandomHandCard());
                    info =  new Info(player, Info.InfoType.BRAQUAGEHAND);
                    checkSuziLafayette();
                }else if (move.selectedPlayer.handCards.size() == 0){
                    moveList = new ArrayList<>();
                    moveList.add(new PickCardMove(move.selectedPlayer.boardCards, 1, PickCardMove.PickType.BRAQUAGE));
                    interactionStack.addFirst(new Action(player, moveList));
                    targets.put(TargetMove.TargetType.BRAQUAGE, move.selectedPlayer);
                }else{
                    moveList = new ArrayList<>();
                    moveList.add(new ChoiceMove(ChoiceMove.Choice.BRAQUAGE));
                    interactionStack.addFirst(new Action(player, moveList));
                    targets.put(TargetMove.TargetType.BRAQUAGE, move.selectedPlayer);
                }
                break;
            case DUEL:
                moveList = new ArrayList<>();
                ArrayList<Card> cards = new ArrayList();
                for(Card card : move.selectedPlayer.handCards){
                    if(card.id == Card_id.BANG || (card.id == Card_id.RATE && move.selectedPlayer.figure.id == fig_id.CALAMITY_JANET)){
                        cards.add(card);
                    }
                }
                moveList.add(new PickCardMove(cards, 1 ,PickCardMove.PickType.DUEL));
                moveList.add(new PassMove(PassMove.PassReason.ENDDUEL));
                info =  new Info(player, Info.InfoType.DUEL, move.selectedPlayer);
                interactionStack.addFirst(new Action(move.selectedPlayer, moveList));
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

                if(defenceNeeded > 0 && move.selectedPlayer.hasBoardCard(Card_id.PLANQUE)){
                    planque = true;
                    if(quickDraw(4)){
                        defenceNeeded--;
                        missPlanque =  true;
                    }
                }

                if(defenceNeeded <= 0){
                    info = new Info(player, Info.InfoType.BANGTARGETFAIL , move.selectedPlayer);
                    interactionStack.addFirst(info);
                    if(jourdonnais){
                        if(missJourdonnais){
                            info = new Info(move.selectedPlayer, Info.InfoType.JOURDONNAISMISSWIN);
                        }else{
                            info = new Info(move.selectedPlayer, Info.InfoType.JOURDONNAISMISSFAIL);
                        }
                        interactionStack.addFirst(info);
                    }
                    if(planque){
                        if(missPlanque){
                            info = new Info(move.selectedPlayer, Info.InfoType.PLANQUEMISSWIN);
                        }else{
                            info = new Info(move.selectedPlayer, Info.InfoType.PLANQUEMISSFAIL);
                        }
                        interactionStack.addFirst(info);
                    }
                    if(slab){
                        info = new Info(move.selectedPlayer, Info.InfoType.SLABBANG);
                        interactionStack.addFirst(info);
                    }
                    info = new Info(player, Info.InfoType.BANGTARGET , move.selectedPlayer);
                    interactionStack.addFirst(info);
                }else {
                    moveList = new ArrayList<>();
                    cards = new ArrayList();
                    for (Card card : move.selectedPlayer.handCards) {
                        if (card.id == Card_id.RATE || (card.id == Card_id.BANG && move.selectedPlayer.figure.id == fig_id.CALAMITY_JANET)) {
                            cards.add(card);
                        }
                    }
                    if (cards.size() >= defenceNeeded) {
                        moveList.add(new PickCardMove(cards, defenceNeeded, PickCardMove.PickType.BANG));
                    }
                    moveList.add(new PassMove(PassMove.PassReason.ENDDUEL));
                    interactionStack.addFirst(new Action(move.selectedPlayer, moveList));
                    info = new Info(move.selectedPlayer, Info.InfoType.BANGDEFENCE);
                    interactionStack.addFirst(info);
                    if (slab && (missPlanque || missJourdonnais)){
                        info = new Info(move.selectedPlayer, Info.InfoType.BANGDEFENCEINEFFICIENT);
                    }
                    if(jourdonnais){
                        if(missJourdonnais){
                            info = new Info(move.selectedPlayer, Info.InfoType.JOURDONNAISMISSWIN);
                        }else{
                            info = new Info(move.selectedPlayer, Info.InfoType.JOURDONNAISMISSFAIL);
                        }
                        interactionStack.addFirst(info);
                    }
                    if(planque){
                        if(missPlanque){
                            info = new Info(move.selectedPlayer, Info.InfoType.PLANQUEMISSWIN);
                        }else{
                            info = new Info(move.selectedPlayer, Info.InfoType.PLANQUEMISSFAIL);
                        }
                        interactionStack.addFirst(info);
                    }
                    if(slab){
                        info = new Info(move.selectedPlayer, Info.InfoType.SLABBANG);
                        interactionStack.addFirst(info);
                    }
                    info = new Info(player, Info.InfoType.BANGTARGET , move.selectedPlayer);
                    interactionStack.addFirst(info);
                }
                break;
        }
    }

    public void pickMoveProcess(Player player, PickCardMove move){
        ArrayList<Move> moveList = new ArrayList<>();
        Info info = null;
        switch(move.pickType){
            case PHASE1CHOOSE:
                info = new Info(player, Info.InfoType.PHASE1CHOOSE);
                moveList.add(new GetCardMove(move.chosenCards));
                for(Card c : move.cardsToGet){
                    if(!move.chosenCards.contains(c)) cardDeque.push(c);
                }
                interactionStack.addFirst(new Action(player,moveList));
                interactionStack.addFirst(info);
                break;
            case THROW:
                for(Card card : move.chosenCards){
                    throwDeque.push(card);
                    player.handCards.remove(card);
                }
                state = State.END;
                break;
            case BRAQUAGE:
                for(Card c : move.chosenCards){
                    player.handCards.add(targets.get(TargetMove.TargetType.BRAQUAGE).removeBoardCard(c.id));
                }
                checkSuziLafayette();
                info = new Info(player, Info.InfoType.BRAQUAGEBOARD, targets.get(TargetMove.TargetType.BRAQUAGE));
                targets.remove(TargetMove.TargetType.BRAQUAGE);
                interactionStack.addFirst(info);
                break;
            case COUPDEFOUDRE:
                for(Card c : move.chosenCards){
                    throwDeque.push(targets.get(TargetMove.TargetType.COUPDEFOUDRE).removeBoardCard(c.id));
                }
                checkSuziLafayette();
                info = new Info(player, Info.InfoType.COUPDEFOUDREBOARD, targets.get(TargetMove.TargetType.COUPDEFOUDRE));
                targets.remove(TargetMove.TargetType.COUPDEFOUDRE);
                interactionStack.addFirst(info);
                break;
            case DUEL:
                Card usedCard = null;
                for(Card c : move.chosenCards){
                    throwDeque.push(c);
                    usedCard = c;
                    player.handCards.remove(c);
                }
                moveList = new ArrayList<>();
                ArrayList<Card> cards = new ArrayList();
                Player opponent = targets.put(TargetMove.TargetType.DUEL,player);
                for(Card card : opponent.handCards){
                    if(card.id == Card_id.BANG || (card.id == Card_id.RATE && opponent.figure.id == fig_id.CALAMITY_JANET)){
                        cards.add(card);
                    }
                }
                moveList.add(new PickCardMove(cards, 1 ,PickCardMove.PickType.DUEL));
                moveList.add(new PassMove(PassMove.PassReason.ENDDUEL));
                info =  new Info(opponent, Info.InfoType.DEFDUEL ,usedCard);
                interactionStack.addFirst(new Action(opponent, moveList));
                interactionStack.addFirst(info);
                break;
            case MAGASIN:
                info = null;
                for(Card card : move.chosenCards){
                    player.handCards.add(card);
                    move.chosenCards.remove(card);
                    info = new Info(player, Info.InfoType.MAGASINPICKED, card);

                }
                if(move.chosenCards.size() > 0){
                    moveList = new ArrayList<>();
                    moveList.add(new PickCardMove(move.chosenCards, 1 ,PickCardMove.PickType.MAGASIN));
                    interactionStack.addFirst(new Action(player.nextPlayer, moveList));
                }
                interactionStack.addFirst(info);
                break;
            case INDIENS:
                info = null;
                for(Card card : move.chosenCards){
                    throwDeque.push(card);
                    player.handCards.remove(card);
                    info = new Info(player, Info.InfoType.DEFINDIENS, card);
                }
                checkSuziLafayette();
                interactionStack.addFirst(info);
                break;
            case GATLING:
                info = null;
                for(Card card : move.chosenCards){
                    throwDeque.push(card);
                    player.handCards.remove(card);
                    info = new Info(player, Info.InfoType.DEFGATLING, card);
                }
                checkSuziLafayette();
                interactionStack.addFirst(info);
                break;
            case BANG:
                info = null;
                for(Card card : move.chosenCards){
                    throwDeque.push(card);
                    player.handCards.remove(card);
                    info = new Info(player, Info.InfoType.DEFBANG, card);
                }
                checkSuziLafayette();
                interactionStack.addFirst(info);
                break;

        }
    }

    public void

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
            case COUPDEFOUDRE:
                if(move.selectedAnswer == ChoiceMove.Answer.HAND){
                    targets.get(TargetMove.TargetType.COUPDEFOUDRE).removeRandomHandCard();
                    info =  new Info(player, Info.InfoType.COUPDEFOUDREHAND, targets.get(TargetMove.TargetType.COUPDEFOUDRE));
                    checkSuziLafayette();
                    targets.remove(TargetMove.TargetType.COUPDEFOUDRE);
                    checkSuziLafayette();
                }else{
                    moveList = new ArrayList<>();
                    moveList.add(new PickCardMove(targets.get(TargetMove.TargetType.COUPDEFOUDRE).boardCards, 1, PickCardMove.PickType.COUPDEFOUDRE));
                    //interactionStack.addFirst(new Action(player, moveList));
                    //info = new Info(player, Info.InfoType.COUPDEFOUDREBOARD, targets.get(TargetMove.TargetType.COUPDEFOUDRE));
                    interactionStack.addFirst(info);
                }
            case BRAQUAGE:

        }
        interactionStack.addFirst(new Action(player,moveList));
        interactionStack.addFirst(info);
    }

    public void passMoveProcess(Player player, PassMove passMove){
        Info info = null;
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
            case ENDDUEL:
                info = new Info(player, Info.InfoType.PASSDUEL);
                player.healthPoint--;
                if(checkMort(player)){
                    stealFromElGringo(player,targets.get(TargetMove.TargetType.DUEL));
                    drawBartCassidy(player, 1);
                }
                targets.remove(TargetMove.TargetType.DUEL);
                break;
            case FAILINDIENS:
                info = new Info(player, Info.InfoType.FAILINDIENS);
                player.healthPoint--;
                if(checkMort(player)){
                    stealFromElGringo(player,currentPlayer);
                    drawBartCassidy(player, 1);
                }
                break;
        }
    }

    public void getCardMoveProcess(Player player, GetCardMove chosenMove){
        for(Card card : chosenMove.cardToGet){
            player.handCards.add(card);
        }
    }


    public boolean checkSuziLafayette(){
        for(Player player : playersList.values()){
            if(player.figure.id == fig_id.SUZY_LAFAYETTE && player.handCards.size()==0){
                ArrayList<Move> moveList = new ArrayList<>();
                ArrayList<Card> card = new ArrayList<>();
                card.add(cardDeque.pop());
                moveList.add(new GetCardMove(card));
                interactionStack.addFirst(new Action(player, moveList));
                interactionStack.addFirst(new Info(player, Info.InfoType.SUZYDRAW));
                return true;
            }
        }
        return false;
    }


    public void stealFromElGringo(Player victim, Player opponent){
        if(victim.figure.id == fig_id.EL_GRINGO){
            ArrayList<Move> moveList = new ArrayList<>();
            ArrayList<Card> cards = new ArrayList<>();
            cards.add(opponent.removeRandomHandCard());
            moveList.add(new GetCardMove(cards));
            interactionStack.addFirst(new Action(victim,moveList));
            Info info =  new Info(victim, Info.InfoType.ELGRINGOSTEAL, opponent);
            interactionStack.addFirst(info);
        }
    }

    public void drawBartCassidy(Player victim, int damageDealt){
        if(victim.figure.id == fig_id.BART_CASSIDY){
            ArrayList<Move> moveList = new ArrayList<>();
            ArrayList<Card> cards = new ArrayList<>();
            for(int i=0; i<damageDealt; i++){
                cards.add(cardDeque.pop());
            }
            moveList.add(new GetCardMove(cards));
            interactionStack.addFirst(new Action(victim,moveList));
            Info info =  new Info(victim, Info.InfoType.BARTCASSIDYDRAW);
            interactionStack.addFirst(info);
        }
    }
}
