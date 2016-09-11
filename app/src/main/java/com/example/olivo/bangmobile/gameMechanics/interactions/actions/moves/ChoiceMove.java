package com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves;

import java.util.ArrayList;

/**
 * Created by olivo on 12/02/2016.
 */
public class ChoiceMove extends Move{
    public Type type = Type.CHOICE;
    public Choice choice;
    public enum Choice {
        ROBBERY,
        LOVESTRIKE,
        PICKPLAYER,
        PICKTRASH,
        SAVEBEER,
        JOURDONNAISABILITY, USEHIDEOUT
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
        this.choice = choice;
        if(choice == Choice.ROBBERY || choice == Choice.LOVESTRIKE){
            availableAnswer.add(Answer.HAND);
            availableAnswer.add(Answer.BOARD);
        }else if(choice == Choice.PICKPLAYER || choice == Choice.PICKTRASH || choice == Choice.SAVEBEER || choice == Choice.USEHIDEOUT ||choice == Choice.JOURDONNAISABILITY){
            availableAnswer.add(Answer.YES);
            availableAnswer.add(Answer.NO);
        }
    }

    public void select(Answer choice){
        selectedAnswer=choice;
    }
}
