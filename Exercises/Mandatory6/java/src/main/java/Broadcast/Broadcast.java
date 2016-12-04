package Broadcast;

import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

/**
 * Created by AndersWindSteffensen on 2016-12-04.
 */
public class Broadcast {
    public static void main(String[] args)
    {
        final ActorSystem system = ActorSystem.create("broadcast");
        final ActorRef person1 = system.actorOf(Props.create(PersonActor.class), "person1");
        final ActorRef person2 = system.actorOf(Props.create(PersonActor.class), "person2");
        final ActorRef person3 = system.actorOf(Props.create(PersonActor.class), "person3");

        final ActorRef broadcaster = system.actorOf(Props.create(BroadcastActor.class), "broadcaster");


        broadcaster.tell(new SubscribeMessage(person1), ActorRef.noSender());
        broadcaster.tell(new SubscribeMessage(person2), ActorRef.noSender());
        broadcaster.tell(new SubscribeMessage(person3), ActorRef.noSender());
        broadcaster.tell(new BroadcastMessage("Shoes half price!!"), ActorRef.noSender());
        broadcaster.tell(new UnsubscribeMessage(person2), ActorRef.noSender());
        broadcaster.tell(new BroadcastMessage("Purses half price!"), ActorRef.noSender());

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
