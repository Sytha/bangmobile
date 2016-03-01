package com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves;

import java.util.ArrayList;

/**
 * Created by olivo on 12/02/2016.
 */
public class ChoiceMove extends Move{
    public Type type = Type.CHOICE;

    public enum Choice {
        STEALPLAYER,
        DELETEPLAYER,
        PICKPLAYER,
        PICKTRASH,
        SAVEBEER
    }

    public enum Answer {
        HAND,
        BOARD,
        YES,
        NO,
    }
    public ArrayList<Answer> availableAnswer;
    public Answer selectedAnswer;

    public ChoiceMove(Choice choice){
        if(choice == Choice.STEALPLAYER || choice == Choice.DELETEPLAYER){
            availableAnswer.add(Answer.HAND);
            availableAnswer.add(Answer.BOARD);
        }else if(choice == Choice.PICKPLAYER || choice == Choice.PICKTRASH || choice == Choice.SAVEBEER){
            availableAnswer.add(Answer.YES);
            availableAnswer.add(Answer.NO);
        }
    }

    public void select(Answer choice){
        selectedAnswer=choice;
    }
}
