package com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves;

import java.util.ArrayList;

/**
 * Created by olivo on 12/02/2016.
 *
 */
public class ChoiceMove extends Move{
    public Type type = Type.CHOICE;
    public Choice choice;
    public enum Choice {
        ROBBERY,
        LOVESTRIKE,
        JESSEJONESPHASE1,
        PEDRORAMIREZPHASE1
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
        }else{
            availableAnswer.add(Answer.YES);
            availableAnswer.add(Answer.NO);
        }
    }

    public void select(Answer choice){
        selectedAnswer=choice;
    }
}
