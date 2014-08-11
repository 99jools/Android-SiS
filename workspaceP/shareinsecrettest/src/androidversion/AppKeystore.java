package androidversion;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.X509EncodedKeySpec;
import java.util.Collections;
import java.util.Enumeration;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by jrs300 on 01/08/14.
 */

public class AppKeystore {

    public static final String KEY_ALGORITHM = "AES";
    public static final int KEY_LENGTH = 128;
    public static final String KEYSTORE_NAME = "SiSKeyStore.ks";
    public static final String KEYSTORE_TYPE = "JCEKS";

    private AppKeystore(){
        //dummy constructor to prevent instantiation
    }

    /**
     * recovers stored key from Keystore and wraps as SecretKeySpec
     * @param groupID
     * @return
     */
    public static SecretKeySpec getKeySpec( String groupID)
    {
        KeyStore ks;
        String appPwd = null;
        try {
            appPwd = AppPwdObj.getInstance().getValue();
        } catch (MissingPwdException e) {
            System.out.println( e.getMessage());
        }

        try {
            //load the keystore
            ks = loadKeyStore(appPwd.toCharArray());    System.out.println("Is keycentry - " + ks.isKeyEntry("mykey"));
            System.out.println(ks.getCertificate("mykey").getPublicKey().toString());
            System.out.println(ks.getCertificate("mykey").getPublicKey().getEncoded());

            /*recover the key entry for the group and package as a SecretKeySpec
             * - errors relating to inability to recover a key are thrown for calling class to handle */
            if (ks.containsAlias(groupID)){
                Key groupKey = ks.getKey(groupID, appPwd.toCharArray());
                return new SecretKeySpec(groupKey.getEncoded(), KEY_ALGORITHM);
            }
        } catch (IOException e) {
          System.out.println(e.getMessage());
        } catch (GeneralSecurityException e) {
        	System.out.println( e.getMessage());
        }
        return null;  //ie not able to recover key
    } //end getExistingKey


    /**
     * THIS CODE IS ONLY APPLICABLE TO JAVA VERSION - does not apply to Android version
     * 
     * Generates a new group encryption key for AES encryption using 256 bit key and stores in KeyStore
     * @param groupID
         * @return
     * @throws MissingPwdException
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public static boolean addGroupKey(String groupID)
            throws MissingPwdException,IOException, GeneralSecurityException{
        String appPwd = AppPwdObj.getInstance().getValue();

        KeyStore ks = loadKeyStore(appPwd.toCharArray());
        if (ks.containsAlias(groupID)) return false;  //group already exists

        // otherwise generate key
        KeyGenerator myKeyGenerator = KeyGenerator.getInstance(KEY_ALGORITHM);
        myKeyGenerator.init(KEY_LENGTH);
        SecretKeySpec newSecretKeySpec =
                new SecretKeySpec(myKeyGenerator.generateKey().getEncoded(), KEY_ALGORITHM);

        /*add to keystore
        KeyStore.SecretKeyEntry skEntry = new KeyStore.SecretKeyEntry(newSecretKeySpec);
        ks.setEntry(groupID, skEntry, new KeyStore.PasswordProtection(appPwd.toCharArray()));
        ks.setEntry("groupZ", skEntry, new KeyStore.PasswordProtection(appPwd.toCharArray()));
        //update stored copy of keystore  (can just rewrite as adding a new group is a rare occurrence)
        writeKeyStore(ks, appPwd.toCharArray());
*/
        //encrypt key and write out encrypted file
        encryptWithPublicKey(newSecretKeySpec, new File("AES key.enc"),"public.crt");
        
        return true;

    } //end addGroupKey

    public static boolean  validate(String appPwd) throws IOException{
        try {
            KeyStore ks = loadKeyStore(appPwd.toCharArray());
            return true;
        } catch (GeneralSecurityException e) {
            return false;
        }
    }

    public static void listGroups() throws MissingPwdException, GeneralSecurityException, IOException {
        KeyStore ks;
        String appPwd = AppPwdObj.getInstance().getValue();

        ks = loadKeyStore(appPwd.toCharArray());
        Enumeration<String> es = ks.aliases();
        for (String key : Collections.list(es)){
        	System.out.println(key + "found");
        }}


    /**
     * Loads the key store from disc
     * @return
     * @throws IOException
     * @throws GeneralSecurityException
     */
    private static KeyStore loadKeyStore(char[] pwd) throws IOException, GeneralSecurityException {
        KeyStore ks = KeyStore.getInstance(KEYSTORE_TYPE);
        FileInputStream fis = null;
        try {

        	 fis = new FileInputStream(KEYSTORE_NAME);
            //    fis = new FileInputStream(KEYSTORE_NAME);
            ks.load(fis, pwd);
        } catch (FileNotFoundException e) {
        	System.out.println("New keystore created");
            //this should only get run if file store doesn't exists at all - creates a new one
            ks.load(null);
        } finally {
            if (fis != null) fis.close();
        }
        return ks;
    } //end loadKeyStore

    /**
     * Updates the stored copy of the keystore - needs to be given a context in order to get access to the Internal Storage
     * @throws GeneralSecurityException
     * @throws IOException
     */
    private static void writeKeyStore(KeyStore ks, char[] pwd) throws GeneralSecurityException, IOException {
        FileOutputStream fos = new FileOutputStream(KEYSTORE_NAME);
        try {
            ks.store(fos, pwd);
        } finally {
            fos.close();
        }
    } //end writeKeyStore
     
	  public static  void encryptWithPublicKey(SecretKeySpec newSecretKeySpec, File out, String publicKeyFile) throws IOException, GeneralSecurityException {
		  // create RSA public key cipher
		    Cipher pkCipher = Cipher.getInstance("RSA");
		    // create AES shared key cipher
		    Cipher aesCipher = Cipher.getInstance("AES");    
		    byte[] aeskey = newSecretKeySpec.getEncoded();
	
		     CertificateFactory cf = CertificateFactory.getInstance("X.509");
		     X509Certificate cert = (X509Certificate)cf.generateCertificate(new FileInputStream(publicKeyFile));
		  
		  // read public key to be used to encrypt the AES key
    		    byte[] encodedKey = new byte[(int)publicKeyFile.length()];
    		    new FileInputStream(publicKeyFile).read(encodedKey);
    		   
    		    PublicKey pk = cert.getPublicKey();
    		   
    		    // write AES key
    		    pkCipher.init(Cipher.ENCRYPT_MODE, pk);
    		    CipherOutputStream os = new CipherOutputStream(new FileOutputStream(out), pkCipher);
    		    os.write(aeskey);
    		    os.close();
    		  }

    } //end AppKeystore
