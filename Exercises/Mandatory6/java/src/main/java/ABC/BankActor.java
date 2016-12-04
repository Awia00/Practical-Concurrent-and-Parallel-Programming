package ABC;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

/**
 * Created by AndersWindSteffensen on 2016-12-04.
 */
public class BankActor extends UntypedActor{
    public void onReceive(Object message) throws Throwable {
        if(message instanceof TransferMessage)
        {
            TransferMessage transfer = (TransferMessage)message;
            transfer.getFrom().tell(new DepositMessage(-transfer.getAmount()), getSelf());
            transfer.getTo().tell(new DepositMessage(transfer.getAmount()), getSelf());
        }
    }
}
