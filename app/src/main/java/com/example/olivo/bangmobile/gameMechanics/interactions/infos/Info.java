package com.example.olivo.bangmobile.gameMechanics.interactions.infos;

import com.example.olivo.bangmobile.gameMechanics.elements.cards.Card;
import com.example.olivo.bangmobile.gameMechanics.elements.Player;
import com.example.olivo.bangmobile.gameMechanics.interactions.Interaction;

import java.util.ArrayList;

/**
 * Created by olivo on 23/02/2016.
 *
 */
public class Info extends Interaction {
    public Player player;
    public InfoType info;
    public Player target;
    public ArrayList<Card> cards;

    public Info(Player player, InfoType info){
        this.player=player;
        this.info = info;
        this.type = Types.INFO;
    }

    public Info(Player player, InfoType info, Player target){
        this.player=player;
        this.info = info;
        this.target = target;
        this.type = Types.INFO;
    }

    public Info(Player player, InfoType info, Card card){
        this.player=player;
        this.info = info;
        this.cards = new ArrayList<>();
        this.cards.add(card);
        this.type = Types.INFO;
    }

    public Info(Player player, InfoType info, ArrayList<Card> cards){
        this.player=player;
        this.info = info;
        this.cards = cards;
        this.type = Types.INFO;
    }



    public enum InfoType {
        START("info.start"),
        PHASE1("info.phase1"),
        PHASE2PLAY("info.phase2Play"),
        THROW("info.throw"),
        DYING("info.dying"),
        DEAD("info.dead"),
        NEXTTURN("info.nextTurn"),
        SHERIFVICTORY("info.victory.sherif"),
        RENEGATEVICTORY("info.victory.renegade"),
        OUTLAWVICTORY("info.victory.outlaw"),
        SHERIFKILLDEPUTY("info.dead.sherifOnDeputy"),
        OUTLAWKILLED("info.dead.outlaw"),
        CARDBANG("info.card.bang"),
        DEFBANGSUCCESS("info.card.bang.def.success"),
        DEFBANGFAIL("info.card.bang.def.fail"),
        SLABBANGREMAINING("info.ability.slabthekiller.remaining"),
        CARDBEER("info.card.beer"),
        BEERHEAL("info.card.beer.heal"),
        BEERUSELESS2P("info.card.beer.useless.2p"),
        BEERUSELESSMAXHEALTH("info.card.beer.useless.maxHealth"),
        CARDMISS("info.card.miss"),
        CARDSALOON("info.card.saloon"),
        SALOONUSELESSHEAL("info.card.saloon.useless"),
        SALOONHEAL("info.saloonHeal"),
        CARDSHOP("info.card.shop"),
        SHOPPICKED("info.card.shop.picked"),
        CARDAPACHE("info.card.apache"),
        DEFAPACHESUCCESS("info.card.apache.def.success"),
        DEFAPACHEFAIL("info.card.apache.def.fail"),
        CARDGATLING("info.card.gatling"),
        DEFGATLINGSUCCESS("info.card.gatling.def.success"),
        DEFGATLINGFAIL("info.card.gatling.def.fail"),
        CARDCONVOY("info.card.convoy"),
        CARDDILIGENCE("info.card.diligence"),
        CARDROBBERY("info.card.robbery"),
        ROBBERYBOARD("info.card.robbery.board"),
        ROBBERYHAND("info.card.robbbery.hand"),
        CARDLOVESTRIKE("info.card.loveStrike"),
        LOVESTRIKEHAND("info.card.loveStrike.hand"),
        LOVESTRIKEBOARD("info.start.loveStrike.board"),
        CARDDUEL("info.card.duel"),
        DUELATTACK("info.card.duel.duelAttack"),
        DEFDUELSUCCESS("info.card.duel.def.success"),
        PASSDUEL("info.card.duel.pass"),
        CARDJAIL("info.card.jail"),
        JAILED("info.card.jail.jailed"),
        JAILCHECK("info.card.jail.check"),
        JAILEVADE("info.card.jail.evade"),
        JAILSTAY("info.card.jail.stay"),
        CARDMUSTANG("info.card.mustang"),
        CARDSCOPE("info.card.scope"),
        CARDDYNAMITE("info.card.dynamite"),
        DYNAMITECHECK("info.card.dynamiteCheck"),
        DYNAMITETHROWED("info.card.dynamiteThrowed"),
        DYNAMITEEXPLODED("info.card.dynamiteExploded"),
        CARDHIDEOUT("info.card.hideOut"),
        HIDEOUTQUICKDRAW("info.card.hideOut.quickDraw"),
        CARDVOLCANIC("info.card.volcanic"),
        CARDSCHOFIELD("info.card.schofield"),
        CARDREMINGTON("info.card.remington"),
        CARDCARBINE("info.card.carbine"),
        CARDWINCHESTER("info.card.winchester"),
        ELGRINGOABILITY("info.ability.elGringo"),
        BARTCASSIDYABILITY("info.ability.bartCassidy"),
        JOURDONNAISABILITY("info.ability.jourdonnais"),
        SLABTHEKILLERABILITY("info.ability.slabTheKiller"),
        SUZYLAFAYETTEABILITY("info.ability.suzyLafayette"),
        VULTURESAMABILITY("info.ability.vultureSam"),
        BLACKJACKABILITYWIN("info.ability.blackJack.win"),
        BLACKJACKABILITYFAIL("info.ability.blackJack.fail"),
        LUCKYDUKEABILITY("info.ability.luckyDuke"),
        KITCARLSONABILITY("info.ability.kitCarlson"),
        PEDRORAMIREZABILITY("info.ability.pedroRamirez"),
        JESSEJONESABILITY("info.ability.jesseJames"),
        SIDKETCHUMABILITY("info.ability.sidKetchum"),
        QUICKDRAWWIN("info.quickdraw.win"),
        QUICKDRAWFAIL("info.quickdraw.win");

        final String name;

        InfoType(String s){
            name=s;
        }
    }
}
