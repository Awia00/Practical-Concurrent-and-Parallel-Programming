package Primer;

import akka.actor.UntypedActor;

/**
 * Created by AndersWindSteffensen on 2016-12-04.
 */
public class SlaveActor extends UntypedActor {

    private final String id;
    public SlaveActor(String id) {
        this.id = id;
    }

    private boolean isPrime(int n)
    {
        int k = 2;
        while(k*k<=n && n%k!=0)
            k++;
        return n>=2 && k*k>n;
    }
    public void onReceive(Object message) throws Throwable {
        if(message instanceof IsPrimeMessage)
        {
            int p = ((IsPrimeMessage) message).getPrimeNumber();
            if(isPrime(p))
            {
                System.out.println("(" + id +") " + p);
            }
        }
    }
}
