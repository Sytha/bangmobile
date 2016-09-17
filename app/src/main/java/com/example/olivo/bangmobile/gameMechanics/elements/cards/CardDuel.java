package com.example.olivo.bangmobile.gameMechanics.elements.cards;

import com.example.olivo.bangmobile.gameMechanics.Game;
import com.example.olivo.bangmobile.gameMechanics.elements.Figure;
import com.example.olivo.bangmobile.gameMechanics.elements.Player;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.Action;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.Move;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.PassMove;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.PickCardMove;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.TargetMove;
import com.example.olivo.bangmobile.gameMechanics.interactions.infos.Info;

import java.util.ArrayList;

/**
 * Created by olivo on 22/08/2016.
 *
 */
public class CardDuel extends Card {
    Player source;
    Player target;
    Player defender;
    boolean dyingPlayer;



    @Override
    public void play(Player source, Game game) {
        game.interactionStack.addLast(new Info(source, Info.InfoType.CARDDUEL));
        ArrayList<Player> targetList = source.getAllOtherTarget();
        ArrayList<Move> moveList = new ArrayList<>();
        moveList.add(new TargetMove(targetList, TargetMove.TargetType.DUEL));
        game.interactionStack.addLast(new Action(source, moveList));
    }

    @Override
    public void action(Player source, Move move, Game game) {
        if(move.type == Move.Type.TARGET){
            TargetMove tMove = (TargetMove) move;
            this.target = tMove.selectedPlayer;
            duelAction(this.source, this.target, game);
        }else if(move.type == Move.Type.PICKCARD){
            PickCardMove pMove = (PickCardMove) move;
            game.interactionStack.addLast(new Info(source, Info.InfoType.DEFDUELSUCCES, defender));
            game.throwDeque.push(source.removeHandCard(pMove.chosenCards.get(0)));
            if(this.defender == this.source){
                duelAction(this.target,this.source,game);
            }else{
                duelAction(this.source,this.target,game);
            }
        }else if(move.type == Move.Type.PASS){
            this.defender.healthPoint--;
            if(game.isDying(this.defender)){
                dyingPlayer=true;
            }else{
                Figure.bartCassidyAbility(this.defender,1,game);
                if(this.defender == target){
                    Figure.elGringoAbility(this.defender,this.source,game);
                }
                actionEnded=true;
                Figure.suziLafayetteAbility(game);
            }
        }else{
            if(dyingPlayer && this.defender.healthPoint>0){
                Figure.bartCassidyAbility(this.defender,1,game);
                if(this.defender == target){
                    Figure.elGringoAbility(this.defender,this.source,game);
                }
            }
            actionEnded=true;
            Figure.suziLafayetteAbility(game);
        }
    }

    private void duelAction(Player attacker, Player defender, Game game){
        game.interactionStack.addLast(new Info(attacker, Info.InfoType.DUEL, defender));
        this.defender=defender;
        ArrayList<Move> moveList = new ArrayList<>();
        ArrayList<Card> cards = new ArrayList<>();
        for(Card card : defender.handCards){
            if(card.id == Card_id.BANG || (card.id == Card_id.MISS && defender.figure.id == Figure.fig_id.CALAMITY_JANET)){
                cards.add(card);
            }
        }
        if(cards.size()>0){
            moveList.add(new PickCardMove(cards, 1 ,PickCardMove.PickType.DUEL));
        }
        moveList.add(new PassMove(PassMove.PassReason.PASSDUEL));
        game.interactionStack.addLast(new Action(defender, moveList));
    }

}
