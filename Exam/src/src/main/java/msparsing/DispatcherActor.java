package msparsing;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

/**
 * Created by ander on 10-01-2017.
 */
public class DispatcherActor extends UntypedActor{
    private ActorRef odd, even;
    @Override
    public void onReceive(Object message) throws Throwable {
        if(message instanceof InitMessage)
        {
            odd = ((InitMessage) message).getOdd();
            even = ((InitMessage) message).getEven();
            odd.tell(message, ActorRef.noSender());
            even.tell(message, ActorRef.noSender());
        }
        else if(message instanceof NumMessage)
        {
            int value = ((NumMessage) message).getNumber();
            if(value % 2 == 0)
            {
                even.tell(message, ActorRef.noSender());
            }
            else
            {
                odd.tell(message, ActorRef.noSender());
            }
        }
    }
}
