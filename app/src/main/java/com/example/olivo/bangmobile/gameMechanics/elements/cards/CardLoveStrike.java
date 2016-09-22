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
 * Created by olivo on 22/08/2016.
 *
 */
public class CardLoveStrike extends Card {
    Player source;
    Player targetPlayer;

    @Override
    public void play(Player source, Game game) {
        this.source = source;
        game.interactionStack.addLast(new Info(source, Info.InfoType.CARDLOVESTRIKE));
        game.throwDeque.push(source.removeHandCard(this));
        ArrayList<Player> targetList = new ArrayList<>(game.players.values());
        ArrayList<Move> moveList = new ArrayList<>();
        moveList.add(new TargetMove(targetList, TargetMove.Target.LOVESTRIKE));
        game.interactionStack.addFirst(new Action(source, moveList));
    }

    @Override
    public void action(Player source, Move move, Game game) {
        if(move.type == Move.Type.TARGET){
            TargetMove tMove = (TargetMove) move;
            this.targetPlayer = tMove.selectedPlayer;
            if(this.targetPlayer.boardCards.size() == 0){
                loveStrikeFromHand(game);
            }else if (this.targetPlayer.handCards.size() == 0){
                askForBoardCard(game);
            }else{
                ArrayList<Move> moveList = new ArrayList<>();
                moveList.add(new ChoiceMove(ChoiceMove.Choice.LOVESTRIKE));
                game.interactionStack.addLast(new Action(source, moveList));
            }
        }else if(move.type == Move.Type.CHOICE){
            ChoiceMove cMove = (ChoiceMove) move;
            if(cMove.selectedAnswer== ChoiceMove.Answer.BOARD){
                askForBoardCard(game);
            }else{
                loveStrikeFromHand(game);
            }
        }else if(move.type == Move.Type.PICKCARD){
            loveStrikeFromBoard((PickCardMove) move,game);
        }
    }

    private void loveStrikeFromBoard(PickCardMove pMove, Game game){
        Card cardToDelete = pMove.chosenCards.get(0);
        game.throwDeque.push(targetPlayer.removeBoardCard(cardToDelete));
        game.interactionStack.addLast(new Info(this.source, Info.InfoType.LOVESTRIKEBOARD, this.targetPlayer));
        game.interactionStack.addLast(new Info(this.source, Info.InfoType.LOVESTRIKEBOARD, cardToDelete));
        Figure.suziLafayetteAbility(game);
        actionEnded=true;
    }

    private void loveStrikeFromHand(Game game){
        game.throwDeque.push(this.targetPlayer.removeRandomHandCard());
        game.interactionStack.addLast(new Info(source, Info.InfoType.LOVESTRIKEHAND, this.targetPlayer));
        Figure.suziLafayetteAbility(game);
        actionEnded=true;
    }

    private void askForBoardCard(Game game){
        ArrayList<Move> moveList = new ArrayList<>();
        moveList.add(new PickCardMove(this.targetPlayer.boardCards, 1, PickCardMove.PickType.LOVESTRIKE));
        game.interactionStack.addLast(new Action(source, moveList));
    }
}
