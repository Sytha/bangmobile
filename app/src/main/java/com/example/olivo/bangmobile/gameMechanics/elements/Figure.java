package com.example.olivo.bangmobile.gameMechanics.elements;

import android.content.Context;
import android.content.res.XmlResourceParser;

import com.example.olivo.bangmobile.R;
import com.example.olivo.bangmobile.gameMechanics.Game;
import com.example.olivo.bangmobile.gameMechanics.elements.cards.Card;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.Action;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.ChoiceMove;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.GetCardMove;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.Move;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.PickCardMove;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.SpecialMove;
import com.example.olivo.bangmobile.gameMechanics.interactions.actions.moves.TargetMove;
import com.example.olivo.bangmobile.gameMechanics.interactions.infos.Info;

import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by olivo on 05/01/2016.
 *
 */
public class Figure {
    public fig_id id;
    public String name;
    public String description;
    public int baseHealthPoint;
    public static ArrayList<Figure> availableFigures=null;

    public enum fig_id {
        SUZY_LAFAYETTE,
        PAUL_REGRET ,
        PEDRO_RAMIREZ ,
        CALAMITY_JANET,
        EL_GRINGO,
        JOURDONNAIS,
        LUCKY_DUKE,
        SLAB_THE_KILLER,
        WILLY_THE_KID,
        JESSE_JONES,
        BART_CASSIDY,
        BLACK_JACK,
        KIT_CARLSON,
        ROSE_DOOLAN,
        SID_KETCHUM,
        VULTURE_SAM
    }

    static public void populateFigure(Context context){
        XmlResourceParser xrp;
        xrp = context.getResources().getXml(R.xml.figure);
        availableFigures = new ArrayList<>();
        Figure figure = null;
        try{
            xrp.next();
            int eventType = xrp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && xrp.getName().equalsIgnoreCase("figure")) {
                    figure = new Figure();
                }else if(eventType == XmlPullParser.START_TAG && xrp.getName().equalsIgnoreCase("name") && figure != null){
                    xrp.next();
                    figure.name=xrp.getText();
                }else if(eventType == XmlPullParser.START_TAG && xrp.getName().equalsIgnoreCase("id") && figure != null){
                    xrp.next();
                    int id=Integer.parseInt(xrp.getText());
                    switch(id){
                        case 1:
                            figure.id= fig_id.SUZY_LAFAYETTE;
                            break;
                        case 2:
                            figure.id= fig_id.PAUL_REGRET;
                            break;
                        case 3:
                            figure.id= fig_id.PEDRO_RAMIREZ;
                            break;
                        case 4:
                            figure.id= fig_id.CALAMITY_JANET;
                            break;
                        case 5:
                            figure.id= fig_id.EL_GRINGO;
                            break;
                        case 6:
                            figure.id= fig_id.JOURDONNAIS;
                            break;
                        case 7:
                            figure.id= fig_id.LUCKY_DUKE;
                            break;
                        case 8:
                            figure.id= fig_id.SLAB_THE_KILLER;
                            break;
                        case 9:
                            figure.id= fig_id.WILLY_THE_KID;
                            break;
                        case 10:
                            figure.id= fig_id.JESSE_JONES;
                            break;
                        case 11:
                            figure.id= fig_id.BART_CASSIDY;
                            break;
                        case 12:
                            figure.id= fig_id.BLACK_JACK;
                            break;
                        case 13:
                            figure.id= fig_id.KIT_CARLSON;
                            break;
                        case 14:
                            figure.id= fig_id.ROSE_DOOLAN;
                            break;
                        case 15:
                            figure.id= fig_id.SID_KETCHUM;
                            break;
                        case 16:
                            figure.id= fig_id.VULTURE_SAM;
                            break;
                    }
                }else if(eventType == XmlPullParser.START_TAG && xrp.getName().equalsIgnoreCase("desc") && figure != null){
                    xrp.next();
                    figure.description=xrp.getText();
                }else if(eventType == XmlPullParser.START_TAG && xrp.getName().equalsIgnoreCase("hp") && figure != null){
                    xrp.next();
                    figure.baseHealthPoint=Integer.parseInt(xrp.getText());
                }else if(eventType == XmlPullParser.END_TAG && xrp.getName().equalsIgnoreCase("figure")){
                    availableFigures.add(figure);
                }
                eventType = xrp.next();
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    static public ArrayList<Figure> getAvailableFigures(Context context){
        if(availableFigures == null) {
            Figure.populateFigure(context);
        }
        return availableFigures;
    }

    public static void bartCassidyAbility(Player victim, int damageDealt, Game game){
        if(victim.figure.id == Figure.fig_id.BART_CASSIDY){
            ArrayList<Move> moveList = new ArrayList<>();
            ArrayList<Card> cards = new ArrayList<>();
            for(int i=0; i<damageDealt; i++){
                cards.add(game.getCardFromDeque());
            }
            moveList.add(new GetCardMove(cards));
            game.interactionStack.addLast(new Action(victim,moveList));
            Info info =  new Info(victim, Info.InfoType.BARTCASSIDYABILITY);
            game.interactionStack.addLast(info);
        }
    }

    public static void elGringoAbility(Player victim, Player opponent, Game game){
        if(victim.figure.id == Figure.fig_id.EL_GRINGO){
            Info info =  new Info(victim, Info.InfoType.ELGRINGOABILITY, opponent);
            game.interactionStack.addLast(info);
            ArrayList<Move> moveList = new ArrayList<>();
            ArrayList<Card> cards = new ArrayList<>();
            cards.add(opponent.removeRandomHandCard());
            moveList.add(new GetCardMove(cards));
            game.interactionStack.addLast(new Action(victim,moveList));
        }
    }

    public static boolean suziLafayetteAbility(Game game){
        for(Player player : game.players.values()){
            if(player.figure.id == Figure.fig_id.SUZY_LAFAYETTE && player.handCards.size()==0){
                game.interactionStack.addLast(new Info(player, Info.InfoType.SUZYLAFAYETTEABILITY));
                ArrayList<Move> moveList = new ArrayList<>();
                ArrayList<Card> card = new ArrayList<>();
                card.add(game.getCardFromDeque());
                moveList.add(new GetCardMove(card));
                game.interactionStack.addLast(new Action(player, moveList));

                return true;
            }
        }
        return false;
    }

    public static void luckyDuckAbility(Game game, Player player, PickCardMove move){
        if(move == null){
            ArrayList<Card> cardsToGet = new ArrayList<>();
            cardsToGet.add(game.getCardFromDeque());
            cardsToGet.add(game.getCardFromDeque());
            ArrayList<Move> moveList = new ArrayList<>();
            moveList.add(new PickCardMove(cardsToGet,1, PickCardMove.PickType.LUCKYDUKEABILITY));
            game.interactionStack.addLast(new Info(player, Info.InfoType.LUCKYDUKEABILITY));
            game.interactionStack.addLast(new Action(player, moveList));
        }else{
            Card chosenCard = move.cardsToGet.get(0);
            game.throwDeque.push(chosenCard);
            move.cardsToGet.remove(chosenCard);
            game.cardDeque.add(move.cardsToGet.get(0));
            game.quickDrawResult = game.checkCardColorAndNumber(chosenCard, game.quickDrawCardColors,game.quickDrawMin,game.quickDrawMax);
            game.quickDrawPending=false;
        }
    }

    public static void jourdonnaisAbility(Game game, Player player){
        game.interactionStack.addLast(new Info(player, Info.InfoType.JOURDONNAISABILITY));
        game.quickDraw(player, new ArrayList<>(Arrays.asList(new Card.CardColor[]{Card.CardColor.HEART})), 1, 13);
    }

    public static boolean specialPhase1(Game game){
        boolean specialPhase1 = false;
        switch(game.currentPlayer.figure.id){
            case BLACK_JACK:
                blackJackAbility(game);
                specialPhase1 = true;
                break;
            case KIT_CARLSON:
                kitCarlsonAbility(game,null);
                specialPhase1 = true;
                break;
            case PEDRO_RAMIREZ:
                if(game.throwDeque.size()>=2){
                    pedroRamirezAbility(game, null);
                    specialPhase1 = true;
                }
                break;
            case JESSE_JONES:
                jesseJonesAbility(game, null);
                specialPhase1 = true;
                break;
        }
        return specialPhase1;
    }

    public static void resumePhase1(Game game, Move move){
        if(move.type == Move.Type.PICKCARD){
            kitCarlsonAbility(game, (PickCardMove) move);
        }else{
            ChoiceMove cMove = (ChoiceMove) move;
            if(cMove.choice == ChoiceMove.Choice.JESSEJONESPHASE1){
                jesseJonesAbility(game,cMove);
            }else{
                pedroRamirezAbility(game,cMove);
            }
        }
    }

    private static void blackJackAbility(Game game){
        ArrayList<Card> cards = new ArrayList<>();
        cards.add(game.getCardFromDeque());
        Card bonusCard = game.getCardFromDeque();
        cards.add(bonusCard);
        if(game.checkCardColorAndNumber(bonusCard, new ArrayList<>(Arrays.asList(new Card.CardColor[]{Card.CardColor.HEART, Card.CardColor.DIAMOND})), 1 , 13)){
            game.interactionStack.addLast(new Info(game.currentPlayer, Info.InfoType.BLACKJACKABILITYWIN, bonusCard));
            cards.add(game.getCardFromDeque());
        }else{
            game.interactionStack.addLast(new Info(game.currentPlayer, Info.InfoType.BLACKJACKABILITYFAIL));
        }
        ArrayList<Move> moveList = new ArrayList<>();
        moveList.add(new GetCardMove(cards));
        game.interactionStack.addLast(new Action(game.currentPlayer, moveList));
        game.state=Game.State.PHASE2;
    }

    private static void kitCarlsonAbility(Game game, PickCardMove move){
        if(move == null){
            ArrayList<Card> cards = new ArrayList<>();
            cards.add(game.getCardFromDeque());
            cards.add(game.getCardFromDeque());
            cards.add(game.getCardFromDeque());
            ArrayList<Move> moveList = new ArrayList<>();
            moveList.add(new PickCardMove(cards,2, PickCardMove.PickType.KITCARLSONPHASE1));
            game.interactionStack.addLast(new Action(game.currentPlayer, moveList));
        }else{
            ArrayList<Card> cards = new ArrayList<>();
            for(Card c : move.chosenCards){
                cards.add(c);
                move.cardsToGet.remove(c);
            }
            for(Card c : move.cardsToGet){
                game.cardDeque.push(c);
            }
            ArrayList<Move> moveList = new ArrayList<>();
            moveList.add(new GetCardMove(cards));
            game.interactionStack.addLast(new Info(game.currentPlayer, Info.InfoType.KITCARLSONABILITY));
            game.interactionStack.addLast(new Action(game.currentPlayer, moveList));
            game.state=Game.State.PHASE2;
        }
    }

    private static void pedroRamirezAbility(Game game, ChoiceMove move){
        if(move == null){
            ArrayList<Move> moveList = new ArrayList<>();
            moveList.add(new ChoiceMove(ChoiceMove.Choice.PEDRORAMIREZPHASE1));
            game.interactionStack.addLast(new Action(game.currentPlayer, moveList));
        }else{
            if(move.selectedAnswer== ChoiceMove.Answer.YES){
                ArrayList<Card> cards = new ArrayList<>();
                cards.add(game.getCardFromDeque());
                cards.add(game.throwDeque.pop());
                ArrayList<Move> moveList = new ArrayList<>();
                moveList.add(new GetCardMove(cards));
                game.interactionStack.addLast(new Info(game.currentPlayer, Info.InfoType.PEDRORAMIREZABILITY));
                game.interactionStack.addLast(new Action(game.currentPlayer,moveList));
                game.state = Game.State.PHASE2;
            }else{
                game.simplePhase1Action();
            }
        }

    }

    private static void jesseJonesAbility(Game game, Move move){
        if(move == null){
            ArrayList<Move> moveList = new ArrayList<>();
            moveList.add(new ChoiceMove(ChoiceMove.Choice.JESSEJONESPHASE1));
            game.interactionStack.addLast(new Action(game.currentPlayer, moveList));
            game.state=Game.State.PHASE2;
        }else{
            if(move.type == Move.Type.CHOICE){
                if(((ChoiceMove)move).selectedAnswer == ChoiceMove.Answer.YES){
                    ArrayList<Player> targets = new ArrayList<>();
                    for(Player p : game.players.values()){
                        if(!p.handCards.isEmpty()){
                            targets.add(p);
                        }
                    }
                    targets.remove(game.currentPlayer);
                    ArrayList<Move> moveList = new ArrayList<>();
                    moveList.add(new TargetMove(targets, TargetMove.Target.JESSEJONESPHASE1));
                    game.interactionStack.addLast(new Info(game.currentPlayer, Info.InfoType.JESSEJONESABILITY));
                    game.interactionStack.addLast(new Action(game.currentPlayer,moveList));
                }else{
                    game.simplePhase1Action();
                }
            }else{
                TargetMove tMove = (TargetMove) move;
                ArrayList<Card> cards = new ArrayList<>();
                cards.add(tMove.selectedPlayer.removeRandomHandCard());
                cards.add(game.getCardFromDeque());
                ArrayList<Move> moveList = new ArrayList<>();
                moveList.add(new GetCardMove(cards));
                game.interactionStack.addLast(new Info(game.currentPlayer, Info.InfoType.JESSEJONESABILITY, tMove.selectedPlayer));
                game.interactionStack.addLast(new Action(game.currentPlayer,moveList));
                game.state = Game.State.PHASE2;
            }
        }
    }

    public static void vultureSamAbility(Player victim, Game game){
        boolean vultureAction=false;
        Player vultureSam=null;
        for(Player p : game.players.values()) {
            if(p.figure.id == fig_id.VULTURE_SAM){
                if (p != victim){
                    vultureAction = true;
                    vultureSam=p;
                }
                break;
            }
        }
        if(vultureAction){
            ArrayList<Card> cardsToGet = new ArrayList<>();
            cardsToGet.addAll(victim.handCards);
            cardsToGet.addAll(victim.boardCards);
            ArrayList<Move> moveList = new ArrayList<>();
            moveList.add(new GetCardMove(cardsToGet));
            game.interactionStack.addLast(new Info(vultureSam, Info.InfoType.VULTURESAMABILITY, victim));
            game.interactionStack.addLast(new Action(vultureSam, moveList));

        }
    }

    public static void sidKetchumAbility(Player player, ArrayList<Move> moveList, Move move ,Game game){
        if(move == null){
            if(player.figure.id == Figure.fig_id.SID_KETCHUM && player.handCards.size()>=2 && player.healthPoint<player.maxHealthPoint){
                moveList.add(new SpecialMove(SpecialMove.Ability.SIDKETCHUMABILITY));
            }
        }else if(move.type == Move.Type.SPECIAL){
            ArrayList<Move> moveListSpecial = new ArrayList<>();
            moveListSpecial.add(new PickCardMove(player.handCards,2, PickCardMove.PickType.SIDKETCHUMABILITY));
            game.interactionStack.addLast(new Info(player, Info.InfoType.SIDKETCHUMABILITY));
            game.interactionStack.addLast(new Action(player, moveListSpecial));
        }else if(move.type == Move.Type.PICKCARD){
            PickCardMove pMove = (PickCardMove) move;
            ArrayList<Card> throwedCards = new ArrayList<>();
            for(Card c : pMove.chosenCards){
                game.throwDeque.push(player.removeHandCard(c));
                throwedCards.add(c);
            }
            player.healthPoint++;
            game.interactionStack.addLast(new Info(player, Info.InfoType.SIDKETCHUMABILITY, throwedCards));
        }
    }
}
