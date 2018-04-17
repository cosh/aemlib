package com.cosh.messaging.aemlib;

import com.microsoft.azure.keyvault.KeyVaultClient;
import com.microsoft.azure.keyvault.models.KeyBundle;
import com.microsoft.azure.keyvault.models.SecretBundle;
import com.microsoft.azure.keyvault.webkey.JsonWebKey;
import com.microsoft.rest.RestClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class KeyVaultTests {

	@Test
	public void keyVaultTest() {

		String vaultBase = "https://<name>.vault.azure.net/";
		String clientId = "<applicationId>";
		String clientSecret = "<secret>";
		KeyVaultConnector kvc = new KeyVaultConnector(vaultBase, clientId, clientSecret);

		kvc.connect();

		System.out.println("Getting Secret");
		SecretBundle secretBundle = kvc.getSecret("someKey", "id");
		System.out.println(secretBundle);

		System.out.println("Getting Key");
		KeyBundle keyBundle = kvc.getKey("fatKey", "id");
		System.out.println(keyBundle);
	}
}
