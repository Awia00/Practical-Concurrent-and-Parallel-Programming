package Ecco;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

/**
 * Created by AndersWindSteffensen on 2016-11-25.
 */
public class Ecco {
    public static void main(String[] args)
    {
        final ActorSystem system = ActorSystem.create("EccoSystem");
        final ActorRef person = system.actorOf(Props.create(PersonActor.class), "person");
        final ActorRef ecco = system.actorOf(Props.create(EccoActor.class), "ecco");
        person.tell(new StartMessage(ecco), ActorRef.noSender());

        try {
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
