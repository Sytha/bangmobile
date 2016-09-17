package com.example.olivo.bangmobile.gameMechanics.elements.cards;

import com.example.olivo.bangmobile.gameMechanics.Game;
import com.example.olivo.bangmobile.gameMechanics.elements.Figure;
import com.example.olivo.bangmobile.gameMechanics.elements.Player;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.Action;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.Move;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.PassMove;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.PickCardMove;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.SpecialMove;
import com.example.olivo.bangmobile.gameMechanics.interactions.infos.Info;

import java.util.ArrayList;

/**
 * Created by olivo on 22/08/2016.
 *
 */
public class CardGatling extends Card {
    Player source;
    Player target;
    boolean targetDying=false;
    boolean quickDrawPending =false;
    boolean jourdonnaisUsed = false;
    boolean hideOutUsed = false;

    @Override
    public void play(Player source, Game game) {
        this.source=source;
        game.throwDeque.push(source.removeHandCard(this));
        game.interactionStack.addLast(new Info(source, Info.InfoType.CARDGATLING));
        Figure.suziLafayetteAbility(game);
        targetNextPlayer(game);
    }

    @Override
    public void action(Player target, Move move, Game game) {
        if(move.type == Move.Type.PICKCARD){
            PickCardMove pMove = (PickCardMove) move;
            for(Card c  : pMove.chosenCards){
                game.throwDeque.add(target.removeHandCard(c));
                game.interactionStack.addLast(new Info(target, Info.InfoType.DEFGATLINGSUCCESS, c));
                Figure.suziLafayetteAbility(game);
            }
            targetNextPlayer(game);
        }else  if(move.type == Move.Type.PASS){
            target.healthPoint -= 1;
            if(game.isDying(target)){
                game.interactionStack.addFirst(new Info(target, Info.InfoType.DEFGATLINGFAIL));
                targetDying=true;
            }else{
                game.interactionStack.addLast(new Info(target, Info.InfoType.DEFGATLINGFAIL));
                Figure.bartCassidyAbility(target,1,game);
                Figure.elGringoAbility(target,source,game);
                targetNextPlayer(game);
            }
        }else if(move.type == Move.Type.CHOICE){
            SpecialMove sMove = (SpecialMove) move;
            if(sMove.ability == SpecialMove.Ability.HIDEOUT){
                hideOutUsed = true;
                CardHideOut hideOutCard = (CardHideOut) target.getCardFromBoard(Card_id.HIDEOUT);
                hideOutCard.action(target,null,game);
                if (game.quickDrawPending) {
                    quickDrawPending =true;
                }else{
                    resumeFromQuickDraw(game.quickDrawResult,game);
                }
            }else if(sMove.ability == SpecialMove.Ability.JOURDONNAISABILITY){
                jourdonnaisUsed=true;
                Figure.jourdonnaisAbility(game,target);
                if (game.quickDrawPending) {
                    quickDrawPending =true;
                }else{
                    resumeFromQuickDraw(game.quickDrawResult,game);
                }
            }
        }else if(quickDrawPending){
            quickDrawPending =false;
            resumeFromQuickDraw(game.quickDrawResult,game);
        }else{
            if(targetDying && target.healthPoint > 0) {
                targetDying = false;
                Figure.bartCassidyAbility(target, 1, game);
                Figure.elGringoAbility(target, source, game);
            }
            targetNextPlayer(game);
        }
    }

    private void targetNextPlayer(Game game){
        if(this.target==null){
            this.target = source.nextPlayer;
        }else{
            this.target = target.nextPlayer;
        }
        jourdonnaisUsed=false;
        hideOutUsed =false;

        targetAction(game);

    }

    private void targetAction(Game game){
        if(this.target != this.source){
            ArrayList<Card> cards = new ArrayList<>();
            boolean hideOut = false;
            for(Card c : target.handCards){
                if(c.id == Card_id.MISS || (c.id == Card_id.BANG && this.target.figure.id == Figure.fig_id.CALAMITY_JANET)){
                    cards.add(c);
                }
                if(c.id == Card_id.HIDEOUT){
                    hideOut = true;
                }
            }
            ArrayList<Move> moveList = new ArrayList<>();
            if(cards.size()>0){
                moveList.add(new PickCardMove(cards,1, PickCardMove.PickType.GATLING));
            }
            if(hideOut &&  !hideOutUsed){
                moveList.add(new SpecialMove(SpecialMove.Ability.HIDEOUT));
            }
            if(target.figure.id == Figure.fig_id.JOURDONNAIS && !jourdonnaisUsed){
                moveList.add(new SpecialMove(SpecialMove.Ability.JOURDONNAISABILITY));
            }
            moveList.add(new PassMove(PassMove.PassReason.DEFGATLINGPASS));
            game.interactionStack.addLast(new Action(this.target,moveList));
        }else{
            actionEnded = true;
        }
    }

    private void resumeFromQuickDraw(boolean hideSuccess, Game game){
        if(hideSuccess){
            game.interactionStack.addLast(new Info(target, Info.InfoType.DEFGATLINGSUCCESS));
            targetNextPlayer(game);
        }else{
            targetAction(game);
        }
    }

}
