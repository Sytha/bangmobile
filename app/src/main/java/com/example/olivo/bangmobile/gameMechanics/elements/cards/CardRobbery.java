package com.example.olivo.bangmobile.gameMechanics.elements.cards;

import com.example.olivo.bangmobile.gameMechanics.Game;
import com.example.olivo.bangmobile.gameMechanics.elements.Figure;
import com.example.olivo.bangmobile.gameMechanics.elements.Player;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.Action;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.ChoiceMove;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.Move;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.PickCardMove;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.TargetMove;
import com.example.olivo.bangmobile.gameMechanics.interactions.infos.Info;

import java.util.ArrayList;

/**
 * Created by olivo on 22/08/2016
 *
 */
public class CardRobbery extends Card {
    Player source;
    Player targetPlayer;

    @Override
    public boolean usable(Player player, Game game) {
        return player.canSteal();
    }

    @Override
    public void play(Player source, Game game) {
        this.source = source;
        game.interactionStack.addLast(new Info(source, Info.InfoType.CARDROBBERY));
        game.throwDeque.push(source.removeHandCard(this));
        ArrayList<Player> targetList = source.getAvailableTarget(source.vision, (int) Math.floor((double) game.players.size() / 2));
        ArrayList<Move> moveList = new ArrayList<>();
        moveList.add(new TargetMove(targetList, TargetMove.TargetType.ROBBERY));
        game.interactionStack.addFirst(new Action(source, moveList));
    }

    @Override
    public void action(Player source, Move move, Game game) {
        if(move.type == Move.Type.TARGET){
            TargetMove tMove = (TargetMove) move;
            this.targetPlayer = tMove.selectedPlayer;
            if(this.targetPlayer.boardCards.size() == 0){
                stealFromHand(game);
            }else if (this.targetPlayer.handCards.size() == 0){
                askForBoardCard(game);
            }else{
                ArrayList<Move> moveList = new ArrayList<>();
                moveList.add(new ChoiceMove(ChoiceMove.Choice.ROBBERY));
                game.interactionStack.addFirst(new Action(source, moveList));
            }
        }else if(move.type == Move.Type.CHOICE){
            ChoiceMove cMove = (ChoiceMove) move;
            if(cMove.selectedAnswer== ChoiceMove.Answer.BOARD){
                askForBoardCard(game);
            }else{
                stealFromHand(game);
            }
        }else if(move.type == Move.Type.PICKCARD){
            stealFromBoard((PickCardMove) move,game);
        }
    }


    private void stealFromBoard(PickCardMove pMove, Game game){
        Card cardToSteal = pMove.chosenCards.get(0);
        source.handCards.add(targetPlayer.removeBoardCard(cardToSteal));
        game.interactionStack.addLast(new Info(this.source, Info.InfoType.ROBBERYBOARD, this.targetPlayer));
        game.interactionStack.addLast(new Info(this.source, Info.InfoType.ROBBERYBOARD, cardToSteal));
        Figure.suziLafayetteAbility(game);
        actionEnded=true;
    }

    private void stealFromHand(Game game){
        source.handCards.add(this.targetPlayer.removeRandomHandCard());
        game.interactionStack.addLast(new Info(source, Info.InfoType.ROBBERYHAND, this.targetPlayer));
        Figure.suziLafayetteAbility(game);
        actionEnded=true;
    }

    private void askForBoardCard(Game game){
        ArrayList<Move> moveList = new ArrayList<>();
        moveList.add(new PickCardMove(this.targetPlayer.boardCards, 1, PickCardMove.PickType.ROBBERY));
        game.interactionStack.addLast(new Action(source, moveList));
    }
}
