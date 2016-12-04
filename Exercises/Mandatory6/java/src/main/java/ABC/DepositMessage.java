package ABC;

import java.io.Serializable;

/**
 * Created by AndersWindSteffensen on 2016-12-04.
 */
public class DepositMessage implements Serializable{
    private int amount;

    public DepositMessage(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }
}
