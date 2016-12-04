package Primer;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AndersWindSteffensen on 2016-12-04.
 */
public class PrimerActor extends UntypedActor{
    private List<ActorRef> slaves;

    private List<ActorRef> createSlaves(int n)
    {
        List<ActorRef> slaves = new ArrayList<ActorRef>();
        for(int i = 0; i<n; i++)
        {
            slaves.add(getContext().actorOf(Props.create(SlaveActor.class, "p" + i)));
        }
        return slaves;
    }

    public void onReceive(Object message) throws Throwable
    {
        if(message instanceof InitMessage)
        {
            int amtOfSlaves = ((InitMessage) message).getAmtOfSlaves();
            if(amtOfSlaves <= 0) throw new RuntimeException("non-positive number");
            slaves = createSlaves(amtOfSlaves);
        }
        else if(message instanceof IsPrimeMessage)
        {
            IsPrimeMessage m = (IsPrimeMessage)message;
            if(m.getPrimeNumber() <= 0) throw new RuntimeException("non positive prime number");
            slaves.get(m.getPrimeNumber()%slaves.size()).tell(m, getSelf());
        }
    }
}
