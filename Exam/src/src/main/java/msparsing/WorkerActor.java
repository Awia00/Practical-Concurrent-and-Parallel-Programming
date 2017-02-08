package msparsing;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

/**
 * Created by ander on 10-01-2017.
 */
public class WorkerActor extends UntypedActor {
    private ActorRef collector;
    @Override
    public void onReceive(Object message) throws Throwable {
        if(message instanceof InitMessage)
        {
            collector = ((InitMessage) message).getCollector();
        }
        else if(message instanceof  NumMessage)
        {
            int value = ((NumMessage) message).getNumber();
            collector.tell(new NumMessage(value*value), ActorRef.noSender());
        }
    }
}
