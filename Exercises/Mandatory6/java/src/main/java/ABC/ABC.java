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
        final ActorRef account1 = system.actorOf(Props.create(AccountActor.class), "account1");
        final ActorRef account2 = system.actorOf(Props.create(AccountActor.class), "account2");
        final ActorRef bank1 = system.actorOf(Props.create(BankActor.class), "bank1");
        final ActorRef bank2 = system.actorOf(Props.create(BankActor.class), "bank2");
        final ActorRef clerk1 = system.actorOf(Props.create(ClerkActor.class), "clerk1");
        final ActorRef clerk2 = system.actorOf(Props.create(ClerkActor.class), "clerk2");

        clerk1.tell(new StartMessage(bank1, account1, account2), ActorRef.noSender());
        clerk2.tell(new StartMessage(bank2, account2, account1), ActorRef.noSender());
        try {
            System.out.println("Press to see results");
            System.in.read();
            account1.tell(new PrintBalanceMessage(), ActorRef.noSender());
            account2.tell(new PrintBalanceMessage(), ActorRef.noSender());
            System.out.println("Press to Terminate");
            System.in.read();
        }catch (Exception e)
        {
            e.printStackTrace();
        } finally {
            system.shutdown();
        }
    }
}
