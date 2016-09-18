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
        ROBBERY("move.choice.robbery"),
        LOVESTRIKE("move.choice.loveStrike"),
        JESSEJONESPHASE1("move.choice.jesseJonesPhase1"),
        PEDRORAMIREZPHASE1("move.choice.pedroRamirezPhase1");

        final String name;

        Choice(String s){
            name=s;
        }
    }

    public enum Answer {
        HAND("move.choice.answer.hand"),
        BOARD("move.choice.answer.board"),
        YES("move.choice.answer.yes"),
        NO("move.choice.answer.no");

        final String name;

        Answer(String s){
            name=s;
        }
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
