package crypto;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
 
 
public class AppKeystore {
 
    public static final String KEY_ALGORITHM = "AES";
    public static final int KEY_LENGTH = 128;
    public static final String KEYSTORE_NAME = "SiSKeyStore.ks";
 
    private AppKeystore(){
        //dummy constructor to prevent instantiation
    }
 
    /**
     * recovers stored key from Keystore and wraps as SecretKeySpec
     * @param groupID
     * @param appPwd
     * @return
     */
    public static SecretKeySpec getKeySpec(String groupID, SharedPrefs prefs) {
        KeyStore ks;
        String appPwd = AppPwdObj.getInstance().getValue();
        try {
            //load the keystore
            ks = loadKeyStore(appPwd.toCharArray());
 
            /*recover the key entry for the group and package as a SecretKeySpec
             * - errors relating to inability to recover a key are thrown for calling class to handle
             */
            if (ks.containsAlias(groupID)){
                Key groupKey = ks.getKey(groupID, appPwd.toCharArray());
                return new SecretKeySpec(groupKey.getEncoded(), KEY_ALGORITHM);
            }
        } catch (IOException e) {
            System.out.println("Problem loading keystore: " + e);
        } catch (GeneralSecurityException e) {
            System.out.println("Problem loading keystore: " + e);
        }
        return null;  //ie not able to recover key
    } //end getExistingKey
 
/**
 * generates a new group encryption key for AES encryption using 256 bit key and stores in KeyStore
 * @param groupID
 * @param appPwd
 * @throws IOException
 * @throws GeneralSecurityException
 */
    public static void addGroupKey(String groupID, SharedPrefs prefs) throws IOException, GeneralSecurityException{
        //load keystore
        String appPwd = AppPwdObj.getInstance().getValue();
        KeyStore ks = loadKeyStore(appPwd.toCharArray());
         
        //generate key
        KeyGenerator myKeyGenerator = KeyGenerator.getInstance(KEY_ALGORITHM);
        myKeyGenerator.init(KEY_LENGTH);
        SecretKeySpec newSecretKeySpec =
            new SecretKeySpec(myKeyGenerator.generateKey().getEncoded(), KEY_ALGORITHM);
         
        //add to keystore
        KeyStore.SecretKeyEntry skEntry = new KeyStore.SecretKeyEntry(newSecretKeySpec);
        ks.setEntry(groupID, skEntry, new KeyStore.PasswordProtection(appPwd.toCharArray()));
 
        //update stored copy of keystore  (can just rewrite as adding a new group is a rare occurrence)
        writeKeyStore(ks, appPwd.toCharArray());
        
        //add new group to shared preferences
        prefs.addGroup(groupID);        
 
        return;
    } //end generateKey
     
         
    /**
     * Loads the key store from disc
     * @return
     * @throws IOException
     * @throws GeneralSecurityException
     */
    private static KeyStore loadKeyStore(char[] pwd)    throws IOException, GeneralSecurityException {
        KeyStore ks = KeyStore.getInstance("JCEKS");
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(KEYSTORE_NAME);
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
     * Updates the stored copy of the keystore
     * @throws GeneralSecurityException
     * @throws NoSuchAlgorithmException
     * @throws CertificateException
     * @throws IOException
     */
        private static void writeKeyStore(KeyStore ks, char[] pwd) throws GeneralSecurityException, NoSuchAlgorithmException, CertificateException, IOException {
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(KEYSTORE_NAME);
                ks.store(fos, pwd);
            } finally {
                if (fos != null) fos.close();
            }
        } //end writeKeyStore
     
} //end AppKeystore
