package com.example.olivo.bangmobile.gameMechanics.interactions.infos;

import com.example.olivo.bangmobile.gameMechanics.elements.cards.Card;
import com.example.olivo.bangmobile.gameMechanics.elements.Player;
import com.example.olivo.bangmobile.gameMechanics.interactions.Interaction;

/**
 * Created by olivo on 23/02/2016.
 */
public class Info extends Interaction {
    public Player player;
    public InfoType info;
    public Player target;
    public Card card;

    public Info(Player player, InfoType info){
        this.player=player;
        this.info = info;
    }

    public Info(Player player, InfoType info, Player target){
        this.player=player;
        this.info = info;
        this.target = target;
    }

    public Info(Player player, InfoType info, Card card){
        this.player=player;
        this.info = info;
        this.card = card;
    }


    public enum InfoType {
        START,
        CHECK_DYNAMITE,
        CHECK_JAIL,
        JAIL_EVADE,
        JAIL_STAY,
        DYNAMITE_THROWED,
        DYNAMITE_EXPLODED,
        PHASE1,
        PHASE1STEAL,
        PHASE1STEALYES,
        PHASE1STEALNO,
        PHASE1TRASH,
        PHASE1TRASHYES,
        PHASE1TRASHNO,
        PHASE1BONUS,
        PHASE1CHOOSE,
        PHASE1CHOOSEDONE,
        PHASE2PLAY,
        LOVESTRIKEHAND,
        LOVESTRIKEBOARD,
        THROW,
        WIN,
        DYING,
        DIE,
        END,
        SUZYDRAW,
        CARDBANG,

        CARDBEER,
        BEERHEAL,

        CARDRATE,

        CARDSALOON,
        SALOONHEAL,

        CARDSHOP,
        SHOPPICKED,

        CARDAPACHE,
        DEFAPACHESUCCESS,
        DEFAPACHEFAIL,

        CARDGATLING,
        DEFGATLINGSUCCESS,
        DEFGATLINGFAIL,

        CARDCONVOI,
        CARDDILIGENCE,
        CARDROBBERY,
        CARDLOVESTRIKE,

        CARDDUEL,
        DUEL,
        DEFDUELSUCCES,
        PASSDUEL,

        CARDJAIL,
        CARDMUSTANG,
        CARDSCOPE,
        CARDDYNAMITE,
        CARDHIDEOUT,
        CARDVOLCANIQUE,
        CARDSCHOFIELD,
        CARDREMINGTON,
        CARDCARBINE,
        ROBBERYHAND,
        CARDWINCHESTER,
        ROBBERYBOARD,
        ELGRINGOSTEAL,
        BARTCASSIDYDRAW,
        JOURDONNAISMISSFAIL,
        JOURDONNAISQUICKDRAW,
        PLANQUEMISSWIN,
        HIDEOUTQUICKDRAW,
        BANGTARGET,
        BANGTARGETFAIL,
        SLABBANG,
        BANGDEFENCE,
        BANGDEFENCEINEFFICIENT,
        DEFBANG,
        ENDBANG,
        DEAD, SHERIFVICTORY, RENEGATEVICTORY, OUTLAWVICTORY, VULTURE, SHERIFKILLDEPUTY, OUTLAWKILLED, LUCKYDUKEDRAW, LUCKYDUKEDRAWDYNAMITE, JAILED, QUICKDRAWWIN, QUICKDRAWFAIL, PHASE1BONUSFAIL, BEERUSELESS2P, BEERUSELESSMAXHEALTH, DEFGATLINGHIDEOUTSUCCESS, DEFGATLINGJOURDONNAISSUCCESS, SALOONUSELESSHEAL,


    }
}
