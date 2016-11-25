package Ecco;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

/**
 * Created by AndersWindSteffensen on 2016-11-25.
 */
public class PersonActor extends UntypedActor{
    public void onReceive(Object message) throws Throwable {
        if(message instanceof  StartMessage)
        {
            StartMessage m = (StartMessage) message;
            ActorRef ecco = m.ecco;
            String s = "Hvad drikker Møller";
            System.out.println("[says]: " + s);
            ecco.tell(new Message(s), getSelf());
        } else if(message instanceof Message)
        {
            Message m = (Message) message;
            System.out.println("[hears]: " + m.s);
        }
    }
}
