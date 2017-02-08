package msparsing;

import akka.actor.ActorRef;

import java.io.Serializable;

/**
 * Created by ander on 10-01-2017.
 */
public class InitMessage implements Serializable {

    private final ActorRef odd;
    private final ActorRef even;
    private final ActorRef collector;

    public InitMessage(ActorRef odd, ActorRef even, ActorRef collector)
    {
        this.odd = odd;
        this.even = even;
        this.collector = collector;
    }

    public ActorRef getOdd() {
        return odd;
    }

    public ActorRef getEven() {
        return even;
    }

    public ActorRef getCollector() {
        return collector;
    }
}
