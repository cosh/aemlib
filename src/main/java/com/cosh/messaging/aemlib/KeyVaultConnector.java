package com.cosh.messaging.aemlib;

import com.microsoft.aad.adal4j.AuthenticationContext;
import com.microsoft.aad.adal4j.AuthenticationResult;
import com.microsoft.aad.adal4j.ClientCredential;
import com.microsoft.azure.keyvault.authentication.KeyVaultCredentials;
import com.microsoft.azure.keyvault.models.KeyBundle;
import com.microsoft.azure.keyvault.models.SecretBundle;
import com.microsoft.azure.keyvault.KeyVaultClient;
import com.microsoft.rest.credentials.ServiceClientCredentials;
import org.apache.commons.lang3.StringUtils;

import java.net.MalformedURLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class KeyVaultConnector {

    private KeyVaultClient client;

    private String _vaultBase;

    private String _clientId;
    private String _clientSecret;

    public KeyVaultConnector(String vaultBase, String clientId, String clientSecret)
    {
        this._vaultBase = vaultBase;
        this._clientId = clientId;
        this._clientSecret = clientSecret;
    }

    public void connect() {

        client = new KeyVaultClient(createCredentials());
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
            if (StringUtils.isNotBlank(_clientId) && StringUtils.isNotBlank(_clientSecret)) {
                ClientCredential credentials = new ClientCredential(_clientId, _clientSecret);
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

    public SecretBundle getSecret(String secretName, String version) {
        return client.getSecret(_vaultBase, secretName, version);
    }

    public KeyBundle getKey(String keyName, String keyVersion) {
        return client.getKey(_vaultBase, keyName, keyVersion);
    }
}
