package ABC;

import akka.actor.UntypedActor;

/**
 * Created by AndersWindSteffensen on 2016-12-04.
 */
public class AccountActor extends UntypedActor{
    private int account = 0;
    public void onReceive(Object message) throws Throwable {
        if(message instanceof PrintBalanceMessage) {
            System.out.println("Account: " + account);
        }
        else if(message instanceof DepositMessage)
        {
            account += ((DepositMessage)message).getAmount();
        }
    }
}
