package com.rohan7055.earnchat.celgram.events;

/**
 * Created by manu on 10/6/2016.
 */
public class AwsEvent {

    private String id;
    private int aws_id,status;
    private boolean error=false;


    public AwsEvent(String id,int aws_id,int status,boolean error){
        this.id=id;
        this.aws_id=aws_id;
        this.status=status;
        this.error=error;
    }

    public String getId(){
        return id;
    }


    public int getAws_id() {
        return aws_id;
    }

    public int getStatus() {
        return status;
    }

    public boolean isError() {
        return error;
    }
}
