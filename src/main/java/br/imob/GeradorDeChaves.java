package br.imob;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;

public class GeradorDeChaves {
    public static void main(String[] args) throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        String privateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
        String publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());

        System.out.println("COPIE ABAIXO PARA O ARQUIVO: app.key");
        System.out.println("-------------------------------------------------");
        System.out.println("-----BEGIN PRIVATE KEY-----");
        System.out.println(privateKey);
        System.out.println("-----END PRIVATE KEY-----");
        System.out.println("-------------------------------------------------");

        System.out.println("\n");

        System.out.println("COPIE ABAIXO PARA O ARQUIVO: app.pub");
        System.out.println("-------------------------------------------------");
        System.out.println("-----BEGIN PUBLIC KEY-----");
        System.out.println(publicKey);
        System.out.println("-----END PUBLIC KEY-----");
        System.out.println("-------------------------------------------------");
    }
}
