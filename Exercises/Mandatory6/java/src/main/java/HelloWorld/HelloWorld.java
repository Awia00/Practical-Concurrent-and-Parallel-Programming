package HelloWorld;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

/**
 * Created by AndersWindSteffensen on 2016-11-25.
 */
public class HelloWorld {
    public static void main(String[] args)
    {
        final ActorSystem system = ActorSystem.create("HelloWorldSystem");
        final ActorRef myactor = system.actorOf(Props.create(MyActor.class), "myactor");
        myactor.tell(new MyMessage("hello"), ActorRef.noSender());
        myactor.tell(new MyMessage("world"), ActorRef.noSender());
        try{
            System.out.println("Press to terminate");
            System.in.read();
        }catch (Exception e)
        {
            e.printStackTrace();
        } finally {
            system.shutdown();
        }
    }
}
