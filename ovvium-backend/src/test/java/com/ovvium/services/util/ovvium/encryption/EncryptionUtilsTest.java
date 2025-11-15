package com.ovvium.services.util.ovvium.encryption;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EncryptionUtilsTest {

    @Test
    public void given_secret_and_value_when_AES_encrypt_then_should_decrypt_correctly() {
        var secret = "my-secret";
        var value ="My awesome value";

        var encrypted = EncryptionUtils.encryptAES(value, secret);

        assertThat(EncryptionUtils.decryptAES(encrypted, secret)).isEqualTo(value);
    }
//
//    @Test
//    public void given_secret_and_encrypted_when_AES_encrypt_then_should_decrypt_correctly() {
//        var secret = "";
//        var value ="9DjdLtz12uY5ewRMt+9M44w5VdFF7OdsuVCWyhVETBdeGCzA8gVVXc4atswd9KuRjOP+bMq53rj771HHCL4T1tlia922iVNjye2fvD9ReBX+yX8q0UqpbcdHYKgzLB0s48UZjfQgvBXmRUE6DT+dQpjKYn3/fIGT4qrV2DSvaT7b992/zvqiWPXt+iDMObWO1lanMXeva8eO/iAJ4O2y4Qd8zY4BYZCHRetC2Y8ZOe7WCzlqMEGPMOF833g/D+2+03Jr1qCamJTnhaVlY/QaD8JM81YJXMhFV3iQmk+wQrGJE99WKJWFIbO9zaSYnEFI/dxttjb/AOEbxKozXtHIRVNIFkl4/8SPN9ZTU0hdmztDFwD2hb9OD5hW5cylLUJRkFhokQNt7K/5xK8Uir9sqpR3vzs46Xs9BFpbPOGi1DQUJOJ5cPvS1n/azeBadegv1ebNz2G8rJBqGkofcXX7flSIwO+5aGpmLRKimC1LgOY=";
//
//        var decrypted = EncryptionUtils.decryptAES(value, secret);
//        System.out.println(decrypted);
//
//        assertThat(decrypted).contains("PaycometWebhookRequest");
//    }
}