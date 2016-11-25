package Ecco;

import java.io.Serializable;

/**
 * Created by AndersWindSteffensen on 2016-11-25.
 */
public class Message implements Serializable {
    public final String s;
    public Message(String s)
    {
        this.s = s;
    }
}
