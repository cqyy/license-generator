package com.shuzhilian.icu.license;

import net.nicholaswilliams.java.licensing.License;
import net.nicholaswilliams.java.licensing.encryption.PasswordProvider;
import net.nicholaswilliams.java.licensing.exception.KeyNotFoundException;
import net.nicholaswilliams.java.licensing.licensor.LicenseCreator;
import net.nicholaswilliams.java.licensing.licensor.LicenseCreatorProperties;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Properties;

/**
 * Created by kali on 6/30/16.
 */
public class LicenseGenerator {

    public static void main(String[] args) throws IOException, ParseException {
        if (args.length != 6){
            System.out.println("Args: propties_file license_path license_name license_password private_key_file private_key_password");
            return;
        }
        String prop = args[0];
        String path = args[1];
        String name = args[2];
        String pawd = args[3];
        String pk   = args[4];
        String pkpd = args[5];

        LicenseGenerator generator = new LicenseGenerator();
        generator.startUp(new File(pk),pkpd.toCharArray());

        Properties conf = new Properties();
        conf.load(new FileInputStream(new File(prop)));
        generator.generateLicense(path,name,pawd.toCharArray(),conf);
    }

    private void startUp(File key,char[] passwd){
        LicenseCreatorProperties.setPrivateKeyDataProvider( () ->{
            try {
                return IOUtils.toByteArray(new FileInputStream(key));
            } catch (IOException e) {
                throw new KeyNotFoundException("Key file was not found." ,e);
            }
        }
          );

        LicenseCreatorProperties.setPrivateKeyPasswordProvider(()->passwd);
    }

    private void generateLicense(String path,String name, char[] password, Properties properties) throws ParseException, IOException {

        String productName = properties.getProperty("ProductName");
        String productId   = properties.getProperty("ProductId");
        String version     = properties.getProperty("Version");
        String holder      = properties.getProperty("Holder");
        String issuser     = properties.getProperty("Issuser");
        String issuseDate  = properties.getProperty("IssuseDate");
        String startDate   = properties.getProperty("StartDate");
        String endDate     = properties.getProperty("EndDate");
        String maxUser     = properties.getProperty("MaxUser");
        String maxCore     = properties.getProperty("MaxCore");
        String maxWorkflow = properties.getProperty("MaxWorkFlow");
        String maxJob      = properties.getProperty("MaxJob");
        String machineId   = properties.getProperty("MachineId");
        String avaliableOperator = properties.getProperty("AvaliableOperator");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");

        License license = new License.Builder()
                .withHolder(holder)
                .withGoodAfterDate(dateFormat.parse(startDate).getTime())
                .withGoodBeforeDate(dateFormat.parse(endDate).getTime())
                .withIssueDate(dateFormat.parse(issuseDate).getTime())
                .withIssuer(issuser)
                .withProductKey(productId)
                .withSubject(productName)
                .withNumberOfLicenses(Integer.valueOf(maxCore))
                .withVersion(Integer.valueOf(version))
                .build();

        byte[] licenseData = LicenseCreator.getInstance().signAndSerializeLicense(license,password);

        try {
            File file = new File(path,name);
            if (!file.getParentFile().exists()){
                file.getParentFile().mkdirs();
            }
            IOUtils.write(licenseData,new FileOutputStream (file));
        } catch (IOException e) {
            throw e;
        }

    }
}
