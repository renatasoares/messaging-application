package com.messaginapp.messaging_application.controller;

import android.util.Log;

import com.virgilsecurity.sdk.cards.CardManager;
import com.virgilsecurity.sdk.cards.validation.CardVerifier;
import com.virgilsecurity.sdk.cards.validation.VerifierCredentials;
import com.virgilsecurity.sdk.cards.validation.VirgilCardVerifier;
import com.virgilsecurity.sdk.cards.validation.Whitelist;
import com.virgilsecurity.sdk.common.TimeSpan;
import com.virgilsecurity.sdk.crypto.AccessTokenSigner;
import com.virgilsecurity.sdk.crypto.CardCrypto;
import com.virgilsecurity.sdk.crypto.PrivateKey;
import com.virgilsecurity.sdk.crypto.VirgilAccessTokenSigner;
import com.virgilsecurity.sdk.crypto.VirgilCardCrypto;
import com.virgilsecurity.sdk.crypto.VirgilCrypto;
import com.virgilsecurity.sdk.crypto.exceptions.CryptoException;
import com.virgilsecurity.sdk.jwt.Jwt;
import com.virgilsecurity.sdk.jwt.JwtGenerator;
import com.virgilsecurity.sdk.jwt.TokenContext;
import com.virgilsecurity.sdk.jwt.accessProviders.CallbackJwtProvider;
import com.virgilsecurity.sdk.jwt.contract.AccessTokenProvider;
import com.virgilsecurity.sdk.utils.ConvertionUtils;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;


public class SetUpCardManager {

    private static final String PUBLIC_KEY_STR = "MCowBQYDK2VwAyEAljOYGANYiVq1WbvVvoYIKtvZi2ji9bAhxyu6iV/LF8M=";
    public static CardCrypto setupCrypto() {
        CardCrypto cardCrypto = new VirgilCardCrypto();

        return cardCrypto;
    }

    private static Jwt generateToken(String identity) throws CryptoException {
        String apiKeyBase64 = "MC4CAQAwBQYDK2VwBCIEIN0K5nifHTF4lniq1DM+CaEw0wbjaXERL8lWy0mfpt4f";

        byte[] apiKeyData = ConvertionUtils.base64ToBytes(apiKeyBase64);

        VirgilCrypto crypto = new VirgilCrypto();
        PrivateKey apiKey = crypto.importPrivateKey(apiKeyData);

        AccessTokenSigner accessTokenSigner = new VirgilAccessTokenSigner();

        String appId = "a879db38688b4ef194d0aec59c4e9397";
        String apiKeyId = "99d57780c1552ccb6ff557542dd3e014";
        TimeSpan ttl = TimeSpan.fromTime(1, TimeUnit.HOURS);

        JwtGenerator jwtGenerator = new JwtGenerator(appId, apiKey, apiKeyId, ttl, accessTokenSigner);

        Jwt tokenUser = jwtGenerator.generateToken(identity);

        return tokenUser;
    }

    public static AccessTokenProvider setupAccessTokenProvider(final String userIdentifier) {

        CallbackJwtProvider.GetTokenCallback getTokenCallback = new CallbackJwtProvider.GetTokenCallback() {

            @Override
            public String onGetToken(TokenContext tokenContext) {
                String token = "";
                try {
                    token = generateToken(userIdentifier).stringRepresentation();
                } catch (CryptoException e) {
                    e.printStackTrace();
                }
                return token;
            }
        };
        AccessTokenProvider accessTokenProvider = new CallbackJwtProvider(getTokenCallback);

        return accessTokenProvider;
    }


    public static CardManager initializeCardManager(CardCrypto cardCrypto, AccessTokenProvider accessTokenProvider,
                                                     CardVerifier cardVerifier) {

        CardManager cardManager = new CardManager(cardCrypto, accessTokenProvider, cardVerifier);

        return cardManager;
    }

}
