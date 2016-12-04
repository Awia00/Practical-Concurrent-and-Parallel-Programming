package Broadcast;

import akka.actor.ActorRef;

import java.io.Serializable;

/**
 * Created by AndersWindSteffensen on 2016-12-04.
 */
public class UnsubscribeMessage implements Serializable {
    private final ActorRef personActor;

    public UnsubscribeMessage(ActorRef personActor) {
        this.personActor = personActor;
    }

    public ActorRef getPersonActor() {
        return personActor;
    }
}
