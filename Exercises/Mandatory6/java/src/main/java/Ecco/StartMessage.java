package Ecco;

import akka.actor.ActorRef;

import java.io.Serializable;

/**
 * Created by AndersWindSteffensen on 2016-11-25.
 */
public class StartMessage implements Serializable {
    public final ActorRef ecco;
    public StartMessage(ActorRef ecco)
    {
        this.ecco = ecco;
    }
}
