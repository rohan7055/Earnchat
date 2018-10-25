package com.rohan7055.earnchat.celgram;

import java.io.Serializable;
import java.util.List;

/**
 * Created by manu on 7/8/2016.
 */
public class ContactsModelWrapper implements Serializable {

    private List<ContactsModel> cm;

    public ContactsModelWrapper(List<ContactsModel> cm){
        this.cm=cm;
    }

    public List<ContactsModel> getCm(){
        return this.cm;
    }
}
