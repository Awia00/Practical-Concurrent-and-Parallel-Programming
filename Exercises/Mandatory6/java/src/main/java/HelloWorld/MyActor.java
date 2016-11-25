package HelloWorld;

import akka.actor.UntypedActor;

/**
 * Created by AndersWindSteffensen on 2016-11-25.
 */
public class MyActor extends UntypedActor {
    private int count = 0;
    public void onReceive(Object o) throws Throwable {
        if(o instanceof MyMessage)
        {
            MyMessage message = (MyMessage) o;
            System.out.println(message.s + "(" + count++ + ")");
        }
    }
}
