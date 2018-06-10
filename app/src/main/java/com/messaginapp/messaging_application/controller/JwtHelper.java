package com.messaginapp.messaging_application.controller;

/**
 * Created by renata on 07/06/18.
 */

import com.virgilsecurity.sdk.common.TimeSpan;
import com.virgilsecurity.sdk.crypto.AccessTokenSigner;
import com.virgilsecurity.sdk.crypto.PrivateKey;
import com.virgilsecurity.sdk.crypto.VirgilAccessTokenSigner;
import com.virgilsecurity.sdk.crypto.VirgilCrypto;
import com.virgilsecurity.sdk.crypto.exceptions.CryptoException;
import com.virgilsecurity.sdk.jwt.Jwt;
import com.virgilsecurity.sdk.jwt.JwtGenerator;
import com.virgilsecurity.sdk.jwt.TokenContext;
import com.virgilsecurity.sdk.jwt.accessProviders.CallbackJwtProvider;
import com.virgilsecurity.sdk.jwt.accessProviders.CallbackJwtProvider.GetTokenCallback;
import com.virgilsecurity.sdk.jwt.contract.AccessTokenProvider;
import com.virgilsecurity.sdk.utils.Base64;
import com.virgilsecurity.sdk.utils.ConvertionUtils;

import java.util.concurrent.TimeUnit;
public class JwtHelper {

    public void run() throws CryptoException {
        setupJwt();
        jwtGeneration();
    }

    private void setupJwt() {
        // Get generated token from server-side
        final String authenticatedQueryToServerSide = "eyJraWQiOiI3MGI0NDdlMzIxZjNhMGZkIiwidHlwIjoiSldUIiwiYWxnIjoiVkVEUzUxMiIsImN0eSI6InZpcmdpbC1qd3Q7dj0xIn0.eyJleHAiOjE1MTg2OTg5MTcsImlzcyI6InZpcmdpbC1iZTAwZTEwZTRlMWY0YmY1OGY5YjRkYzg1ZDc5Yzc3YSIsInN1YiI6ImlkZW50aXR5LUFsaWNlIiwiaWF0IjoxNTE4NjEyNTE3fQ.MFEwDQYJYIZIAWUDBAIDBQAEQP4Yo3yjmt8WWJ5mqs3Yrqc_VzG6nBtrW2KIjP-kxiIJL_7Wv0pqty7PDbDoGhkX8CJa6UOdyn3rBWRvMK7p7Ak";

        // Setup AccessTokenProvider
        GetTokenCallback getTokenCallback = new GetTokenCallback() {

            @Override
            public String onGetToken(TokenContext tokenContext) {
                return authenticatedQueryToServerSide;
            }
        };
        AccessTokenProvider accessTokenProvider = new CallbackJwtProvider(getTokenCallback);
    }

    private void jwtGeneration() throws CryptoException {
        // API_KEY
        String apiKeyBase64 = "MC4CAQAwBQYDK2VwBCIEIGdmjG9eMULbAKFnIynPCYt2LZB71r0DKdjTMD3X6sYt";
        byte[] apiKeyData = ConvertionUtils.base64ToBytes(apiKeyBase64);

        // import a private key
        VirgilCrypto crypto = new VirgilCrypto();
        PrivateKey apiKey = crypto.importPrivateKey(apiKeyData);

        AccessTokenSigner accessTokenSigner = new VirgilAccessTokenSigner();

        String appId = "b45b686542de4441a2106f88228b5e33"; // APP_ID
        String apiKeyId = "5aa6877b1652a7633c10d458bf4c2cc9"; // API_KEY_ID
        TimeSpan ttl = TimeSpan.fromTime(1, TimeUnit.HOURS); // 1 hour

        // setup JWT generator
        JwtGenerator jwtGenerator = new JwtGenerator(appId, apiKey, apiKeyId, ttl, accessTokenSigner);

    }
}