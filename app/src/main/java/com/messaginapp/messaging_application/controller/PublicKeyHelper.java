package com.messaginapp.messaging_application.controller;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.virgilsecurity.sdk.cards.Card;
import com.virgilsecurity.sdk.cards.CardManager;
import com.virgilsecurity.sdk.cards.validation.CardVerifier;
import com.virgilsecurity.sdk.cards.validation.VirgilCardVerifier;
import com.virgilsecurity.sdk.client.exceptions.VirgilServiceException;
import com.virgilsecurity.sdk.crypto.CardCrypto;
import com.virgilsecurity.sdk.crypto.PrivateKey;
import com.virgilsecurity.sdk.crypto.VirgilCrypto;
import com.virgilsecurity.sdk.crypto.VirgilKeyPair;
import com.virgilsecurity.sdk.crypto.VirgilPrivateKey;
import com.virgilsecurity.sdk.crypto.VirgilPrivateKeyExporter;
import com.virgilsecurity.sdk.crypto.VirgilPublicKey;
import com.virgilsecurity.sdk.crypto.exceptions.CryptoException;
import com.virgilsecurity.sdk.jwt.contract.AccessTokenProvider;
import com.virgilsecurity.sdk.storage.JsonFileKeyStorage;
import com.virgilsecurity.sdk.storage.KeyStorage;
import com.virgilsecurity.sdk.storage.PrivateKeyStorage;
import com.virgilsecurity.sdk.utils.ConvertionUtils;
import com.virgilsecurity.sdk.utils.Tuple;


public class PublicKeyHelper {

    private static PrivateKeyStorage privateKeyStorage;
    private static Context context;
    private static CardManager cardManager;


    public static  synchronized CardManager getCardManager(String userIdentifier){
        if(cardManager == null){
            SetUpCardManager setUp = new SetUpCardManager();
            CardCrypto cardCrypto = setUp.setupCrypto();
            AccessTokenProvider accessTokenProvider = setUp.setupAccessTokenProvider(userIdentifier);
            cardManager = setUp.initializeCardManager(cardCrypto, accessTokenProvider, new VirgilCardVerifier(cardCrypto));
        }
        return cardManager;
    }
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

    public void createCard(String userIdentifier, Context context) throws CryptoException {
        VirgilCrypto crypto = new VirgilCrypto();

        VirgilKeyPair keyPair = crypto.generateKeys();

        if (!getPrivateKeyStorage(getContextInstance(context)).exists(userIdentifier)) {
            getPrivateKeyStorage(getContextInstance(context)).store(keyPair.getPrivateKey(), userIdentifier, null);
        }

        try {
            Card card = getCardManager(userIdentifier).publishCard(keyPair.getPrivateKey(), keyPair.getPublicKey(), userIdentifier);
        } catch (CryptoException | VirgilServiceException e) {
            e.printStackTrace();
        }
    }


    public List<Card> searchCardByIdentity(String userIdentifier) {
        List<Card> cards = null;
        try {
            cards = getCardManager(userIdentifier).searchCards(userIdentifier);
        } catch (CryptoException | VirgilServiceException e) {
            e.printStackTrace();
        }
        return cards;
    }

    public String signThenEncrypt(String currentUser, String messageToEncrypt, String userId1, String userId2, Context context) throws CryptoException {
        VirgilCrypto crypto = new VirgilCrypto();
        byte[] dataToEncrypt = ConvertionUtils.toBytes(messageToEncrypt);
        byte[] encryptedData = new byte[0];

        Tuple<PrivateKey, Map<String, String>> senderPrivateKeyEntry = getPrivateKeyStorage(getContextInstance(context)).load(userId1);
        VirgilPrivateKey senderPrivateKey = (VirgilPrivateKey) senderPrivateKeyEntry.getLeft();

        try {
            List<Card> cards = getCardManager(currentUser).searchCards(userId2);
            List<VirgilPublicKey> receiverRelevantCardsPublicKeys = new ArrayList<>();
            for (Card card : cards) {
                if (!card.isOutdated()) {
                    receiverRelevantCardsPublicKeys.add((VirgilPublicKey) card.getPublicKey());
                }
            }
            encryptedData = crypto.signThenEncrypt(dataToEncrypt, senderPrivateKey, receiverRelevantCardsPublicKeys);

        } catch (CryptoException | VirgilServiceException e) {
            e.printStackTrace();
        }

        return encryptedData.toString();
    }


    public String decryptThenVerify(String currentUser, String messagePlainText, String userId1, String userId2, Context context) throws CryptoException {
        VirgilCrypto crypto = new VirgilCrypto();

        byte[] encryptedData = ConvertionUtils.toBytes(messagePlainText);
        byte[] decryptedData = new byte[0];

        Tuple<PrivateKey, Map<String, String>> receiverPrivateKeyEntry = getPrivateKeyStorage(getContextInstance(context)).load(userId2);
        VirgilPrivateKey receiverPrivateKey = (VirgilPrivateKey) receiverPrivateKeyEntry.getLeft();

        try {
            List<Card> cards = getCardManager(currentUser).searchCards(userId1);
            List<VirgilPublicKey> senderRelevantCardsPublicKeys = new ArrayList<>();
            for (Card card : cards) {
                if (!card.isOutdated()) {
                    senderRelevantCardsPublicKeys.add((VirgilPublicKey) card.getPublicKey());
                }
            }

            decryptedData = crypto.decryptThenVerify(encryptedData, receiverPrivateKey, senderRelevantCardsPublicKeys);
        } catch (CryptoException | VirgilServiceException e) {
            e.printStackTrace();
        }

        return decryptedData.toString();
    }
}
