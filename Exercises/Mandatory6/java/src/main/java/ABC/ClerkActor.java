package ABC;

import akka.actor.UntypedActor;

/**
 * Created by AndersWindSteffensen on 2016-12-04.
 */
public class ClerkActor extends UntypedActor{
    private static 
    public void onReceive(Object message) throws Throwable {
        if(message instanceof StartMessage)
        {
            StartMessage start = (StartMessage)message;

        }
    }
}
