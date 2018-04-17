package com.cosh.messaging.aemlib.key;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("keyvault")
public class KeyVaultConfig {

    private String vaultBase;

    private String clientId;
    private String clientSecret;

    public String getVaultBase() {
        return vaultBase;
    }

    public void setVaultBase(String vaultBase) {
        this.vaultBase = vaultBase;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }
}
