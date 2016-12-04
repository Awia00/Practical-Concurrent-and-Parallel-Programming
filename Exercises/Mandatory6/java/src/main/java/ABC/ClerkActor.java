package ABC;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

import java.util.Random;

/**
 * Created by AndersWindSteffensen on 2016-12-04.
 */
public class ClerkActor extends UntypedActor{
    private Random random;

    private void nTransfers(int number, ActorRef bank, ActorRef from, ActorRef to)
    {
        for(int i = 0; i<number; i++)
        {
            bank.tell(new TransferMessage(from, to, random.nextInt(100)), getSelf());
        }
    }

    public void onReceive(Object message) throws Throwable {
        if(message instanceof StartMessage)
        {
            random = new Random(System.currentTimeMillis());
            StartMessage start = (StartMessage)message;
            nTransfers(100, start.getBank(), start.getFrom(), start.getTo());
        }
    }
}
