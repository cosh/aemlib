package com.cosh.messaging.aemlib.key;

import com.microsoft.aad.adal4j.AuthenticationContext;
import com.microsoft.aad.adal4j.AuthenticationResult;
import com.microsoft.aad.adal4j.ClientCredential;
import com.microsoft.azure.keyvault.authentication.KeyVaultCredentials;
import com.microsoft.azure.keyvault.models.KeyBundle;
import com.microsoft.azure.keyvault.models.SecretBundle;
import com.microsoft.azure.keyvault.KeyVaultClient;
import com.microsoft.azure.keyvault.requests.SetSecretRequest;
import com.microsoft.rest.credentials.ServiceClientCredentials;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.MalformedURLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class KeyVaultConnector {

    private KeyVaultClient _client;

    private final KeyVaultConfig _config;

    @Autowired
    public KeyVaultConnector(KeyVaultConfig config)
    {
        this._config = config;
    }

    public void connect() {

        _client = new KeyVaultClient(createCredentials());
    }

    private ServiceClientCredentials createCredentials() {
        return new KeyVaultCredentials() {

            //Callback that supplies the token type and access token on request.
            @Override
            public String doAuthenticate(String authorization, String resource, String scope) {

                AuthenticationResult authResult;
                try {
                    authResult = getAccessToken(authorization, resource);
                    return authResult.getAccessToken();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return "";
            }

        };
    }

    private AuthenticationResult getAccessToken(String authorization, String resource) throws InterruptedException, ExecutionException, MalformedURLException {

        AuthenticationResult result = null;

        //Starts a service to fetch access token.
        ExecutorService service = null;
        try {
            service = Executors.newFixedThreadPool(1);
            AuthenticationContext context = new AuthenticationContext(authorization, false, service);

            Future<AuthenticationResult> future = null;

            //Acquires token based on client ID and client secret.
            if (StringUtils.isNotBlank(_config.getClientId()) && StringUtils.isNotBlank(_config.getClientSecret())) {
                ClientCredential credentials = new ClientCredential(_config.getClientId(), _config.getClientSecret());
                future = context.acquireToken(resource, credentials, null);
            }

            result = future.get();
        } finally {
            service.shutdown();
        }

        if (result == null) {
            throw new RuntimeException("Authentication results were null.");
        }
        return result;
    }

    public SecretBundle getSessionSecret(String sessionSecretName, String version) {
        return _client.getSecret(_config.getVaultBase(), sessionSecretName, version);
    }


    public KeyBundle getSessionEncryptionKey(String sessionEncryptionKeyname, String keyVersion) {
        return _client.getKey(_config.getVaultBase(), sessionEncryptionKeyname, keyVersion);
    }

    public KeyBundle getSessionEncryptionKey(String sessionEncryptionKeyname) {
        return _client.getKey(_config.getVaultBase(), sessionEncryptionKeyname);
    }

    public SecretBundle createSessionSecret(String sessionEncryptionKeyname, String secretName) {

        final KeyBundle keyBundle = getSessionEncryptionKey(sessionEncryptionKeyname);

        final SetSecretRequest secretRequest = new SetSecretRequest.Builder(_config.getVaultBase(), secretName, generateSessionSecret(keyBundle))
                .build();
        return _client.setSecret(secretRequest);
    }

    private String generateSessionSecret(KeyBundle keyBundle) {
        return null;
    }
}
