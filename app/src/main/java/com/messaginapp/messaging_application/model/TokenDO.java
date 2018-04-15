package com.messaginapp.messaging_application.model;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.util.List;
import java.util.Map;
import java.util.Set;

@DynamoDBTable(tableName = "appmessaging-mobilehub-742744033-token")

public class TokenDO {
    private String _userId;
    private Long _tTL;
    private String _qrCode;


    public TokenDO(String _userId, Long _tTL, String _qrCode) {
        this._userId = _userId;
        this._tTL = _tTL;
        this._qrCode = _qrCode;
    }


    @DynamoDBHashKey(attributeName = "userId")
    @DynamoDBAttribute(attributeName = "userId")
    public String getUserId() {
        return _userId;
    }

    public void setUserId(final String _userId) {
        this._userId = _userId;
    }
    @DynamoDBAttribute(attributeName = "TTL")
    public Long getTTL() {
        return _tTL;
    }

    public void setTTL(final Long _tTL) {
        this._tTL = _tTL;
    }
    @DynamoDBAttribute(attributeName = "qrCode")
    public String getQrCode() {
        return _qrCode;
    }

    public void setQrCode(final String _qrCode) {
        this._qrCode = _qrCode;
    }

}