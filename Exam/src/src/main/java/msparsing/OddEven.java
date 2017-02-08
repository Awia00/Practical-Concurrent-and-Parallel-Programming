package msparsing;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

/**
 * Created by ander on 10-01-2017.
 */

public class OddEven {

    public static void main(String[] args)
    {
        final ActorSystem system = ActorSystem.create("OddEven");
        final ActorRef dispatcher = system.actorOf(Props.create(DispatcherActor.class), "dispather");
        final ActorRef odd = system.actorOf(Props.create(WorkerActor.class), "odd");
        final ActorRef even = system.actorOf(Props.create(WorkerActor.class), "even");
        final ActorRef collector = system.actorOf(Props.create(CollectorActor.class), "collector");

        dispatcher.tell(new InitMessage(odd, even, collector), ActorRef.noSender());

        for(int i = 1; i<=10; i++)
        {
            dispatcher.tell(new NumMessage(i), ActorRef.noSender());
        }
        try {
            System.out.println("Press to Terminate");
            System.in.read();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            system.shutdown();
        }
    }
}
