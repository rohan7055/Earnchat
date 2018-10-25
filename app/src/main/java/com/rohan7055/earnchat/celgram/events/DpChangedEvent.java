package com.rohan7055.earnchat.celgram.events;

/**
 * Created by ron on 3/28/2017.
 */

public class DpChangedEvent
{
    private boolean isDpChanged;
    public DpChangedEvent(boolean isDpChanged){this.isDpChanged=isDpChanged;}
    public boolean isdpchange(){return isDpChanged;}

}
