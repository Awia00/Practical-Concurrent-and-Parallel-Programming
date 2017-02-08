package msparsing;

import java.io.Serializable;

/**
 * Created by ander on 10-01-2017.
 */
public class NumMessage implements Serializable {

    private final int number;

    public NumMessage(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }
}
