package com.messaginapp.messaging_application.controller;

import android.content.Context;
import android.util.Log;

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
import com.virgilsecurity.sdk.utils.Base64;
import com.virgilsecurity.sdk.utils.ConvertionUtils;
import com.virgilsecurity.sdk.utils.Tuple;

import java.util.Map;

/**
 * Created by renata on 07/06/18.
 */

public class KeyStorageHelper {

    private static PrivateKeyStorage privateKeyStorage;
    private static Context context;

    public  static synchronized Context getContextInstance(Context argContext) {
        if(context == null){
            context = argContext;
        }
        return context;
    }

    public static synchronized PrivateKeyStorage getPrivateKeyStorage(Context context) throws CryptoException {
       if(privateKeyStorage == null){
           VirgilCrypto crypto = new VirgilCrypto();
           VirgilPrivateKeyExporter privateKeyExporter = new VirgilPrivateKeyExporter(crypto);
           KeyStorage keyStorage = new JsonFileKeyStorage(getContextInstance(context).getFilesDir().getAbsolutePath());
           privateKeyStorage = new PrivateKeyStorage(privateKeyExporter, keyStorage);
       }

       return privateKeyStorage;
    }

    public void generatePrivateKey(String userIdentifier, Context context) throws CryptoException {
        VirgilCrypto crypto = new VirgilCrypto();
        VirgilKeyPair keyPair = crypto.generateKeys();
        VirgilPrivateKey privateKey = keyPair.getPrivateKey();
        if (!getPrivateKeyStorage(context).exists(userIdentifier)) {
            getPrivateKeyStorage(context).store(privateKey, userIdentifier, null);
        }
    }

    public VirgilPrivateKey getPrivateKey(String userIdentifier, Context context) throws CryptoException {
        Tuple<PrivateKey, Map<String, String>> privateKeyEntry = getPrivateKeyStorage(context).load(userIdentifier);
        VirgilPrivateKey privateKey = (VirgilPrivateKey) privateKeyEntry.getLeft();

        return privateKey;
    }



    public VirgilPublicKey getPublicKey(String userIdentifier) throws CryptoException {
        //String publickey = exportPublicKey();
        VirgilCrypto crypto = new VirgilCrypto();
        String example = ConvertionUtils.toBase64String(userIdentifier);

        byte[] publicKeyData = ConvertionUtils.toBase64Bytes(userIdentifier);
        VirgilPublicKey publicKey = crypto.importPublicKey(publicKeyData);

        Log.d("CRYPTO publicKey2", publicKey.toString());
        return publicKey;
    }



}
