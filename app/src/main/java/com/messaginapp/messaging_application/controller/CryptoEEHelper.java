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
import com.virgilsecurity.sdk.crypto.VirgilCardCrypto;
import com.virgilsecurity.sdk.crypto.VirgilCrypto;
import com.virgilsecurity.sdk.crypto.VirgilKeyPair;
import com.virgilsecurity.sdk.crypto.VirgilPrivateKey;
import com.virgilsecurity.sdk.crypto.VirgilPrivateKeyExporter;
import com.virgilsecurity.sdk.crypto.VirgilPublicKey;
import com.virgilsecurity.sdk.crypto.exceptions.CryptoException;
import com.virgilsecurity.sdk.crypto.exceptions.EncryptionException;
import com.virgilsecurity.sdk.jwt.contract.AccessTokenProvider;
import com.virgilsecurity.sdk.storage.JsonFileKeyStorage;
import com.virgilsecurity.sdk.storage.KeyStorage;
import com.virgilsecurity.sdk.storage.PrivateKeyStorage;
import com.virgilsecurity.sdk.utils.ConvertionUtils;
import com.virgilsecurity.sdk.utils.Tuple;


public class CryptoEEHelper {

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

    public VirgilCrypto getVirgilCrypto(String userIdentifier) {
        return ((VirgilCardCrypto) getCardManager(userIdentifier).getCrypto()).getVirgilCrypto();
    }

    public String decrypt(String text, String currentUser, Context context) {
        byte[] cipherData = ConvertionUtils.base64ToBytes(text);

        try {
            byte[] decryptedData =
                    getVirgilCrypto(currentUser).decrypt(cipherData,
                            (VirgilPrivateKey) getPrivateKeyStorage(context).load(
                                    currentUser)
                                    .getLeft());
            return ConvertionUtils.toString(decryptedData);
        } catch (CryptoException e) {
            e.printStackTrace();
            return "Message encrypted";
        }
    }

    public String encrypt(String currentUser, String data, String receiver) {
        byte[] toEncrypt = ConvertionUtils.toBytes(data);
        byte[] encryptedData = new byte[0];
        try {
            List<Card> cardsSender = getCardManager(currentUser).searchCards(currentUser);
            List<Card> cardsReceiver =getCardManager(currentUser).searchCards(receiver);

            List<VirgilPublicKey> senderRelevantCardsPublicKeys = new ArrayList<>();
            for (Card card : cardsSender) {
                if (!card.isOutdated()) {
                    senderRelevantCardsPublicKeys.add((VirgilPublicKey) card.getPublicKey());
                }
            }
            for (Card card : cardsReceiver) {
                if (!card.isOutdated()) {
                    senderRelevantCardsPublicKeys.add((VirgilPublicKey) card.getPublicKey());
                }
            }
            encryptedData = getVirgilCrypto(currentUser).encrypt(toEncrypt, senderRelevantCardsPublicKeys);
        } catch (EncryptionException e) {
            e.printStackTrace();
        } catch (CryptoException e) {
            e.printStackTrace();
        } catch (VirgilServiceException e) {
            e.printStackTrace();
        }
        return ConvertionUtils.toBase64String(encryptedData);
    }
}
