package com.shuzhilian.icu.license;

import net.nicholaswilliams.java.licensing.*;
import net.nicholaswilliams.java.licensing.exception.InvalidLicenseException;
import net.nicholaswilliams.java.licensing.exception.KeyNotFoundException;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by kali on 6/30/16.
 */
public class LicenseViewer {

    public static void main(String[] args) {
        if (args.length != 4){
            System.out.println("Args: publicKey keyPassword licenseName licensePassword");
            return;
        }
        String keyFile = args[0];
        String keyPwd  = args[1];
        String license = args[2];
        String licPwd  = args[3];

        LicenseViewer  viewer = new LicenseViewer();
        viewer.startUp(new File(keyFile),keyPwd.toCharArray(),licPwd.toCharArray());
        viewer.printLicense(license);
    }

    private void startUp(File key, char[] keyPasswd, char[] licensePasswd) {
        LicenseManagerProperties.setPublicKeyDataProvider(() -> {
            try {
                return IOUtils.toByteArray(new FileInputStream(key));
            } catch (IOException e) {
                throw new KeyNotFoundException("Read key data failed.", e);
            }
        });
        LicenseManagerProperties.setPublicKeyPasswordProvider( () -> keyPasswd);

        LicenseManagerProperties.setLicenseProvider( (context)-> new FileLicenseProvider().getLicense(context));
        LicenseManagerProperties.setLicensePasswordProvider( ()-> licensePasswd);

        LicenseManagerProperties.setLicenseValidator(license -> {

        });
        LicenseManagerProperties.setCacheTimeInMinutes(5);
        LicenseManager.getInstance();
    }

    private void printLicense(String name){
        LicenseManager licenseManager = LicenseManager.getInstance();
        License license = licenseManager.getLicense(name);

        try {
            licenseManager.validateLicense(license);
        }catch (Throwable throwable){
            throwable.printStackTrace();
        }

        System.out.println(license);
    }
}
