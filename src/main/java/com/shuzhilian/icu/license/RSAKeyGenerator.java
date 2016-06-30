package com.shuzhilian.icu.license;

import net.nicholaswilliams.java.licensing.encryption.RSAKeyPairGenerator;

import java.io.IOException;
import java.security.KeyPair;

/**
 * Created by kali on 6/29/16.
 */
public class RSAKeyGenerator {

    public static void main(String[] args) {

        if (args.length != 2){
            System.out.println("Args: password_for_public_key,password_for_private_key");
            return;
        }
        String po   = args[0];
        String pi   = args[1];
        try {
            RSAKeyPairGenerator generator = new RSAKeyPairGenerator();
            KeyPair keyPair = generator.generateKeyPair();
            generator.saveKeyPairToFiles(keyPair,"private.key","public.key",pi.toCharArray(),po.toCharArray());
        }  catch (IOException e) {
            e.printStackTrace();
        }
    }
}
