package Broadcast;

import akka.actor.UntypedActor;

/**
 * Created by AndersWindSteffensen on 2016-12-04.
 */
public class PersonActor extends UntypedActor {

    public void onReceive(Object message) throws Throwable {
        if(message instanceof BroadcastMessage)
        {
            BroadcastMessage m = (BroadcastMessage)message;
            System.out.println(m.s);
        }
    }
}
