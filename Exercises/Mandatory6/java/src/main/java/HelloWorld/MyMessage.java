package HelloWorld;

import java.io.Serializable;

/**
 * Created by AndersWindSteffensen on 2016-11-25.
 */
public class MyMessage implements Serializable {
    public final String s;
    public MyMessage(String s) {
        this.s = s;
    }
}
