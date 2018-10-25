package com.rohan7055.earnchat.celgram;

import java.io.Serializable;

/**
 * Created by ron on 5/1/2017.
 */
public class MagicContactsHolder implements Serializable
{
    String number;

    public MagicContactsHolder() {

}
    public MagicContactsHolder(String number) {
              this.number=number;
    }


    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
