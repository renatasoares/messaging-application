package com.messaginapp.messaging_application.model;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

@DynamoDBTable(tableName = "appmessaging-mobilehub-742744033-notificationToken")

public class NotificationTokenDO {
    private String _userIdNotifcation;
    private String _tokenNotifcation;

    public NotificationTokenDO(String _userIdNotifcation, String _tokenNotifcation) {
        this._userIdNotifcation = _userIdNotifcation;
        this._tokenNotifcation = _tokenNotifcation;
    }

    @DynamoDBHashKey(attributeName = "userIdNotifcation")
    @DynamoDBAttribute(attributeName = "userIdNotifcation")
    public String getUserIdNotifcation() {
        return _userIdNotifcation;
    }

    public void setUserIdNotifcation(final String _userIdNotifcation) {
        this._userIdNotifcation = _userIdNotifcation;
    }
    @DynamoDBAttribute(attributeName = "tokenNotifcation")
    public String getTokenNotifcation() {
        return _tokenNotifcation;
    }

    public void setTokenNotifcation(final String _tokenNotifcation) {
        this._tokenNotifcation = _tokenNotifcation;
    }

}
