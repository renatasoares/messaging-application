package com.messaginapp.messaging_application.controller;

import com.virgilsecurity.sdk.crypto.PrivateKey;
import com.virgilsecurity.sdk.crypto.VirgilCrypto;
import com.virgilsecurity.sdk.crypto.VirgilKeyPair;
import com.virgilsecurity.sdk.crypto.VirgilPrivateKey;
import com.virgilsecurity.sdk.crypto.VirgilPrivateKeyExporter;
import com.virgilsecurity.sdk.crypto.VirgilPublicKey;
import com.virgilsecurity.sdk.crypto.exceptions.CryptoException;
import com.virgilsecurity.sdk.storage.JsonFileKeyStorage;
import com.virgilsecurity.sdk.storage.KeyStorage;
import com.virgilsecurity.sdk.storage.PrivateKeyStorage;
import com.virgilsecurity.sdk.utils.ConvertionUtils;
import com.virgilsecurity.sdk.utils.Tuple;

import java.util.Map;

/**
 * Created by renata on 07/06/18.
 */

public class KeyStorageHelper {

    private static PrivateKeyStorage privateKeyStorage;

    public static synchronized PrivateKeyStorage getPrivateKeyStorage() throws CryptoException {
       if(privateKeyStorage == null){
           VirgilCrypto crypto = new VirgilCrypto();
           VirgilPrivateKeyExporter privateKeyExporter = new VirgilPrivateKeyExporter(crypto);
           KeyStorage keyStorage = new JsonFileKeyStorage();
           privateKeyStorage = new PrivateKeyStorage(privateKeyExporter, keyStorage);
       }

       return privateKeyStorage;
    }

    public void generatePrivateKey(String userIdentifier) throws CryptoException {
        VirgilCrypto crypto = new VirgilCrypto();
        VirgilKeyPair keyPair = crypto.generateKeys();
        VirgilPrivateKey privateKey = keyPair.getPrivateKey();

        getPrivateKeyStorage().store(privateKey, userIdentifier , null);
    }

    public VirgilPrivateKey getPrivateKey(String userIdentifier) throws CryptoException {
        Tuple<PrivateKey, Map<String, String>> privateKeyEntry = getPrivateKeyStorage().load(userIdentifier);
        VirgilPrivateKey privateKey = (VirgilPrivateKey) privateKeyEntry.getLeft();

        return privateKey;
    }

    public VirgilPublicKey getPublicKey(String userIdentifier) throws CryptoException {
        VirgilCrypto crypto = new VirgilCrypto();
        byte[] publicKeyData = ConvertionUtils.base64ToBytes(userIdentifier);
        VirgilPublicKey publicKey = crypto.importPublicKey(publicKeyData);

        return publicKey;
    }

}
