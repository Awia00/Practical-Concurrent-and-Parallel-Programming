package ABC;

import akka.actor.ActorRef;

/**
 * Created by AndersWindSteffensen on 2016-12-04.
 */
public class TransferMessage {
    private final ActorRef from, to;
    private final int amount;

    public TransferMessage(ActorRef from, ActorRef to, int amount) {
        this.from = from;
        this.to = to;
        this.amount = amount;
    }

    public ActorRef getFrom() {
        return from;
    }

    public ActorRef getTo() {
        return to;
    }

    public int getAmount() {
        return amount;
    }
}
