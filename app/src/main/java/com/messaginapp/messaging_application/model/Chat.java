package com.messaginapp.messaging_application.model;

/**
 * Created by renata on 21/05/18.
 */

public class Chat {
    public Chat(){

    }
    public Chat(String identifierUser1, String identifierUser2) {
        this.identifierUser1 = identifierUser1;
        this.identifierUser2 = identifierUser2;
    }

    public String getIdentifierUser1() {
        return identifierUser1;
    }

    public void setIdentifierUser1(String identifierUser1) {
        this.identifierUser1 = identifierUser1;
    }

    public String getIdentifierUser2() {
        return identifierUser2;
    }

    public void setIdentifierUser2(String identifierUser2) {
        this.identifierUser2 = identifierUser2;
    }

    private String identifierUser1;
    private String identifierUser2;
}
