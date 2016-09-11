package com.example.olivo.bangmobile.gameMechanics.elements.cards;

import com.example.olivo.bangmobile.gameMechanics.Game;
import com.example.olivo.bangmobile.gameMechanics.elements.Figure;
import com.example.olivo.bangmobile.gameMechanics.elements.Player;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.Action;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.Move;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.PassMove;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.PickCardMove;
import com.example.olivo.bangmobile.gameMechanics.interactions.infos.Info;

import java.util.ArrayList;

/**
 * Created by olivo on 22/08/2016.
 */
public class CardApache extends Card {
    Player source;
    Player target;
    boolean targetDying=false;

    @Override
    public void play(Player source, ArrayList<Player> targetsList, Game game) {
        this.source=source;
        game.throwDeque.push(source.removeHandCard(this));
        game.interactionStack.addLast(new Info(source, Info.InfoType.CARDAPACHE));
        Figure.checkSuziLafayette(game);
        targetNextPlayer(game);
    }

    @Override
    public void action(Player target, Move move, Game game) {
        if(move.type == Move.Type.PICKCARD){
            PickCardMove pMove = (PickCardMove) move;
            for(Card c  : pMove.chosenCards){
                game.throwDeque.add(target.removeHandCard(c));
                game.interactionStack.addLast(new Info(target, Info.InfoType.DEFAPACHESUCCESS, c));
                Figure.checkSuziLafayette(game);
            }
            targetNextPlayer(game);
        }else  if(move.type == Move.Type.PASS){
            target.healthPoint -= 1;
            if(game.isDying(target)){
                game.interactionStack.addFirst(new Info(target, Info.InfoType.DEFAPACHEFAIL));
                targetDying=true;
            }else{
                game.interactionStack.addLast(new Info(target, Info.InfoType.DEFAPACHEFAIL));
                Figure.drawBartCassidy(target,1,game);
                Figure.stealFromElGringo(target,source,game);
                targetNextPlayer(game);
            }
        }else{
            if(targetDying && target.healthPoint > 0) {
                targetDying = false;
                Figure.drawBartCassidy(target, 1, game);
                Figure.stealFromElGringo(target, source, game);
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

        if(this.target != this.source){
            ArrayList<Card> cards = new ArrayList<>();
            for(Card c : target.handCards){
                if(c.id == Card_id.BANG || (c.id == Card_id.MISS && this.target.figure.id == Figure.fig_id.CALAMITY_JANET)){
                    cards.add(c);
                }
            }
            ArrayList<Move> moveList = new ArrayList<>();
            moveList.add(new PickCardMove(cards,1, PickCardMove.PickType.APACHE));
            moveList.add(new PassMove(PassMove.PassReason.DEFAPACHEPASS));
            game.interactionStack.addLast(new Action(this.target,moveList));
        }else{
            actionEnded = true;
        }
    }
}