package Broadcast;

import java.io.Serializable;

/**
 * Created by AndersWindSteffensen on 2016-12-04.
 */
public class BroadcastMessage  implements Serializable {
    public final String s;
    public BroadcastMessage(String s)
    {
        this.s = s;
    }
}
