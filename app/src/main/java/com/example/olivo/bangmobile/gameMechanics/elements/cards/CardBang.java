package com.example.olivo.bangmobile.gameMechanics.elements.cards;

import com.example.olivo.bangmobile.gameMechanics.Game;
import com.example.olivo.bangmobile.gameMechanics.elements.Figure;
import com.example.olivo.bangmobile.gameMechanics.elements.Player;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.Action;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.Move;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.PassMove;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.PickCardMove;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.SpecialMove;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.TargetMove;
import com.example.olivo.bangmobile.gameMechanics.interactions.infos.Info;

import java.util.ArrayList;

/**
 * Created by olivo on 22/08/2016.
 *
 */
public class CardBang extends Card {
    Player source;
    Player target;
    int defenceNeeded;
    boolean targetDying = false;
    boolean quickDrawPending = false;
    boolean jourdonnaisUsed = false;
    boolean hideOutUsed = false;


    @Override
    public boolean usable(Player player, Game game) {
        return (player.canBang((int) Math.floor((double) game.players.values().size() / 2)) && (game.bangUsed < game.bangLimit || player.unlimitedBang));
    }

    @Override
    public void play(Player source, Game game) {
        this.source = source;
        game.interactionStack.addLast(new Info(this.source, Info.InfoType.CARDBANG));
        if (this.source.figure.id == Figure.fig_id.SLAB_THE_KILLER) {
            defenceNeeded = 2;
            game.interactionStack.addLast(new Info(this.source, Info.InfoType.SLABBANG));
        } else {
            defenceNeeded = 1;
        }
        ArrayList<Move> moveList = new ArrayList<>();
        moveList.add(new TargetMove(this.source.getAvailableTarget(this.source.vision + this.source.weaponVision, (int) Math.floor((double) game.players.values().size() / 2)), TargetMove.TargetType.BANG));
        game.interactionStack.add(new Action(this.source, moveList));
        game.bangUsed++;
        Figure.suziLafayetteAbility(game);
    }

    @Override
    public void action(Player source, Move move, Game game) {
        if (move.type == Move.Type.TARGET) {
            this.target = ((TargetMove) move).selectedPlayer;
            this.targetAction(game);
        } else if (move.type == Move.Type.PASS) {
            target.healthPoint -= 1;
            if (game.isDying(target)) {
                game.interactionStack.addFirst(new Info(target, Info.InfoType.DEFBANGFAIL));
                targetDying = true;
            } else {
                game.interactionStack.addLast(new Info(target, Info.InfoType.DEFBANGFAIL));
                Figure.bartCassidyAbility(target, 1, game);
                Figure.elGringoAbility(target, source, game);
                actionEnded = true;
            }
        } else if (move.type == Move.Type.SPECIAL) {
            SpecialMove sMove = (SpecialMove) move;
            if (sMove.ability == SpecialMove.Ability.HIDEOUT) {
                hideOutUsed = true;
                CardHideOut hideOutCard = (CardHideOut) target.getCardFromBoard(Card_id.HIDEOUT);
                hideOutCard.action(target, null, game);
                if (game.quickDrawPending) {
                    quickDrawPending = true;
                } else {
                    resumeFromQuickDraw(game.quickDrawResult, game);
                }
            } else if (sMove.ability == SpecialMove.Ability.JOURDONNAISABILITY) {
                jourdonnaisUsed = true;
                Figure.jourdonnaisAbility(game, target);
                if (game.quickDrawPending) {
                    quickDrawPending = true;
                } else {
                    resumeFromQuickDraw(game.quickDrawResult, game);
                }
            }
        } else if (quickDrawPending) {
            quickDrawPending = false;
            resumeFromQuickDraw(game.quickDrawResult, game);
        } else if (move.type == Move.Type.PICKCARD) {
            PickCardMove pMove = (PickCardMove) move;
            for (Card c : pMove.chosenCards) {
                game.throwDeque.add(target.removeHandCard(c));
                Figure.suziLafayetteAbility(game);
                defenceNeeded--;
                if (defenceNeeded == 0) {
                    game.interactionStack.addLast(new Info(target, Info.InfoType.DEFBANGSUCCESS, c));
                } else {
                    game.interactionStack.addLast(new Info(target, Info.InfoType.SLABBANGREMAINING, c));
                    targetAction(game);
                }
            }
        } else{
            if(targetDying && target.healthPoint > 0) {
                targetDying = false;
                Figure.bartCassidyAbility(target, 1, game);
                Figure.elGringoAbility(target, source, game);
            }
            actionEnded=true;
        }
    }

    @Override
    public void reset(){
        actionEnded=false;
        source=null;
        target=null;
        defenceNeeded=1;
        targetDying = false;
        quickDrawPending = false;
        jourdonnaisUsed = false;
        hideOutUsed = false;
    }

    private void targetAction(Game game) {
        ArrayList<Card> cards = new ArrayList<>();
        boolean hideOut = false;
        for (Card c : target.handCards) {
            if (c.id == Card_id.MISS || (c.id == Card_id.BANG && this.target.figure.id == Figure.fig_id.CALAMITY_JANET)) {
                cards.add(c);
            }
            if (c.id == Card_id.HIDEOUT) {
                hideOut = true;
            }
        }
        ArrayList<Move> moveList = new ArrayList<>();
        if (cards.size() > 0) {
            moveList.add(new PickCardMove(cards, 1, PickCardMove.PickType.BANG));
        }
        if (hideOut && !hideOutUsed) {
            hideOutUsed=true;
            moveList.add(new SpecialMove(SpecialMove.Ability.HIDEOUT));
        }
        if (target.figure.id == Figure.fig_id.JOURDONNAIS && !jourdonnaisUsed) {
            jourdonnaisUsed = true;
            moveList.add(new SpecialMove(SpecialMove.Ability.JOURDONNAISABILITY));
        }
        moveList.add(new PassMove(PassMove.PassReason.BANGPASS));
        game.interactionStack.addLast(new Action(this.target, moveList));
    }

    private void resumeFromQuickDraw(boolean hideSuccess, Game game) {
        if (hideSuccess) {
            defenceNeeded--;
            if (defenceNeeded == 0) {
                game.interactionStack.addLast(new Info(target, Info.InfoType.DEFBANGSUCCESS));
                actionEnded = true;
            } else {
                game.interactionStack.addLast(new Info(target, Info.InfoType.SLABBANGREMAINING));
                targetAction(game);
            }
        } else {
            targetAction(game);
        }
    }
}
