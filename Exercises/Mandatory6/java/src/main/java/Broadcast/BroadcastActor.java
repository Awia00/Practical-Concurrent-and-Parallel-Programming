package Broadcast;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AndersWindSteffensen on 2016-12-04.
 */
public class BroadcastActor extends UntypedActor {
    private List<ActorRef> persons;

    public BroadcastActor() {
        this.persons = new ArrayList<ActorRef>();
    }

    public void onReceive(Object message) throws Throwable {
        if(message instanceof BroadcastMessage)
        {
            for (ActorRef actor : persons) {
                actor.tell(message, ActorRef.noSender());
            }
        }
        else if(message instanceof  SubscribeMessage)
        {
            SubscribeMessage sm = (SubscribeMessage)message;
            persons.add(sm.getPersonActor());
        }
        else if(message instanceof  UnsubscribeMessage)
        {
            UnsubscribeMessage sm = (UnsubscribeMessage)message;
            persons.remove(sm.getPersonActor());
        }
    }
}
