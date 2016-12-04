package Primer;

import java.io.Serializable;

/**
 * Created by AndersWindSteffensen on 2016-12-04.
 */
public class IsPrimeMessage implements Serializable {
    private final int primeNumber;

    public IsPrimeMessage(int primeNumber) {
        this.primeNumber = primeNumber;
    }

    public int getPrimeNumber() {
        return primeNumber;
    }
}
