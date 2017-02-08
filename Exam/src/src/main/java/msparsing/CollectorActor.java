package msparsing;

import akka.actor.UntypedActor;

/**
 * Created by ander on 10-01-2017.
 */
public class CollectorActor extends UntypedActor {
    @Override
    public void onReceive(Object message) throws Throwable {
        if(message instanceof  NumMessage) {
            System.out.println(((NumMessage) message).getNumber());
        }
    }
}
