package com.messaginapp.messaging_application.controller;

import com.virgilsecurity.sdk.crypto.VirgilCrypto;
import com.virgilsecurity.sdk.crypto.VirgilPrivateKey;
import com.virgilsecurity.sdk.crypto.VirgilPublicKey;
import com.virgilsecurity.sdk.crypto.exceptions.CryptoException;
import com.virgilsecurity.sdk.utils.ConvertionUtils;

import java.util.Arrays;

/**
 * Created by renata on 07/06/18.
 */

public class CryptoHelper {

    public String encrypt(String messagePlainText, String idUser1, String idUser2) {
        VirgilCrypto crypto = new VirgilCrypto();
        byte[] dataToEncrypt = ConvertionUtils.toBytes(messagePlainText);

        KeyStorageHelper keyHelper = new KeyStorageHelper();
        VirgilPrivateKey senderPrivateKey = null;
        VirgilPublicKey receiverPublicKey = null;
        byte[] encryptedData = new byte[0];

        try {
            senderPrivateKey = keyHelper.getPrivateKey(idUser1);
            receiverPublicKey = keyHelper.getPublicKey(idUser2);
            encryptedData = crypto.signThenEncrypt(dataToEncrypt, senderPrivateKey, receiverPublicKey);
        } catch (CryptoException e) {
            e.printStackTrace();
        }

        return encryptedData.toString();
    }

    public String decryptThenVerify(String encryptedDataS, String idUser1, String idUser2) throws CryptoException {

        byte[] encryptedData = ConvertionUtils.toBytes(encryptedDataS);
        VirgilCrypto crypto = new VirgilCrypto();
        KeyStorageHelper keyHelper = new KeyStorageHelper();
        VirgilPrivateKey receiverPrivateKey = null;
        VirgilPublicKey senderPublicKey = null;
        byte[] decryptedData = new byte[0];

        try {
            receiverPrivateKey = keyHelper.getPrivateKey(idUser1);
            senderPublicKey = keyHelper.getPublicKey(idUser2);
            decryptedData = crypto.decryptThenVerify(encryptedData, receiverPrivateKey, Arrays.asList(senderPublicKey));
        } catch (CryptoException e) {
            e.printStackTrace();
        }

        String decryptedMessage = ConvertionUtils.toString(decryptedData);

        return decryptedMessage;
    }

}
