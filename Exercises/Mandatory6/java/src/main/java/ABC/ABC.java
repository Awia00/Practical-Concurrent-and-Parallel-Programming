package ABC;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

/**
 * Created by AndersWindSteffensen on 2016-12-04.
 */
public class ABC {
    public static void main(String[] args)
    {
        final ActorSystem system = ActorSystem.create("ABC");
        final ActorRef person1 = system.actorOf(Props.create(AccountActor.class), "person1");
        final ActorRef person2 = system.actorOf(Props.create(AccountActor.class), "person2");
        final ActorRef person3 = system.actorOf(Props.create(AccountActor.class), "person3");
        final ActorRef bank1 = system.actorOf(Props.create(BankActor.class), "bank1");
        final ActorRef bank2 = system.actorOf(Props.create(BankActor.class), "bank2");
        final ActorRef clerk1 = system.actorOf(Props.create(ClerkActor.class), "clerk1");
        final ActorRef clerk2 = system.actorOf(Props.create(ClerkActor.class), "clerk2");

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
