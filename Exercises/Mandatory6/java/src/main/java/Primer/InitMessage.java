package Primer;

import java.io.Serializable;

/**
 * Created by AndersWindSteffensen on 2016-12-04.
 */
public class InitMessage implements Serializable{
    private final int amtOfSlaves;

    public InitMessage(int amtOfSlaves) {
        this.amtOfSlaves = amtOfSlaves;
    }

    public int getAmtOfSlaves() {
        return amtOfSlaves;
    }
}
