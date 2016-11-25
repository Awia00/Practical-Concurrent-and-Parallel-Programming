package Ecco;

import akka.actor.UntypedActor;

/**
 * Created by AndersWindSteffensen on 2016-11-25.
 */
public class EccoActor extends UntypedActor{
    public void onReceive(Object message) throws Throwable {
        if(message instanceof Message)
        {
            Message m = (Message) message;
            String s = m.s;
            Message reply = s.length() > 5 ? new Message("..." + s.substring(s.length()-5)) : new Message("...");
            getSender().tell(reply, getSelf());
            getSender().tell(reply, getSelf());
            getSender().tell(reply, getSelf());
        }
    }
}
