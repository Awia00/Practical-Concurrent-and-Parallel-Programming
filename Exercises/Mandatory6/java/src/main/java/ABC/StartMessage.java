package ABC;

import akka.actor.ActorRef;

/**
 * Created by AndersWindSteffensen on 2016-12-04.
 */
public class StartMessage {
    private final ActorRef bank, from, to;

    public StartMessage(ActorRef bank, ActorRef from, ActorRef to) {
        this.bank = bank;
        this.from = from;
        this.to = to;
    }

    public ActorRef getBank() {
        return bank;
    }

    public ActorRef getFrom() {
        return from;
    }

    public ActorRef getTo() {
        return to;
    }
}
