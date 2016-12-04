package Primer;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

/**
 * Created by AndersWindSteffensen on 2016-12-04.
 */
public class Primer {

    private static void spam(ActorRef primeActor, int min, int max)
    {
        for(int i = min; i<max; i++)
        {
            primeActor.tell(new IsPrimeMessage(i), ActorRef.noSender());
        }
    }
    public static void main(String[] args)
    {
        final ActorSystem system = ActorSystem.create("primer");
        final ActorRef primer = system.actorOf(Props.create(PrimerActor.class), "Primer");


        try {
            primer.tell(new InitMessage(7), ActorRef.noSender());
            spam(primer, 2, 100);
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
