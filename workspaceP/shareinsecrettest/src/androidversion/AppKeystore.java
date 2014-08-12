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
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Collections;
import java.util.Enumeration;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by jrs300 on 01/08/14.
 */

public class AppKeystore {

    public static final String KEY_ALGORITHM = "AES";
    public static final String PUBKEY_ALGORITHM = "RSA";
    public static final int KEY_LENGTH = 128;
    public static final String KEYSTORE_NAME = "SiSKeyStore.ks";
    public static final String KEYSTORE_TYPE = "JCEKS";
    
    private char[] appPwdAsArray;
    private KeyStore ks;

    private AppKeystore() throws MissingPwdException, IOException, GeneralSecurityException{
    	   String appPwd = AppPwdObj.getInstance().getValue();
    	   this.appPwdAsArray = appPwd.toCharArray();
           this.ks = loadKeyStore();
    }
    
    
/**
 * Retrieves the key from the Keystore corresponding to the supplied alias and wraps it as SecretKeySpec
 * @param alias
 * @return KeySpec for the retrieved key
 */
    public SecretKeySpec getSKS(String alias){
		Key key = getKey(alias);
		return new SecretKeySpec(key.getEncoded(), KEY_ALGORITHM);
    }
  
    
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
    public boolean addGroupKey(String groupID)
            throws MissingPwdException,IOException, GeneralSecurityException{
    
        if (ks.containsAlias(groupID)) return false;  //group already exists

        // otherwise generate a new key
        SecretKeySpec sks = genSKS();
        
        //encrypt key and write out encrypted file - THIS ONLY USED FOR TESTING - WILL NEED TO BE MODIFIED
        encryptWithPublicKey(sks, new File("AES key.enc"),"public.crt");

        //add to key store
        KeyStore.SecretKeyEntry skEntry = new KeyStore.SecretKeyEntry(sks);
        ks.setEntry(groupID, skEntry, new KeyStore.PasswordProtection(appPwdAsArray));

        //update stored copy of keystore  (can just rewrite as adding a new group is a rare occurrence)
        writeKeyStore();
  
        return true;
    } //end addGroupKey

    

    /**
     * with reference to code from http://www.macs.hw.ac.uk/~ml355/lore/pkencryption.htm 
     */
    public void importGroupKey(String groupID, File groupKeyFile) throws GeneralSecurityException, IOException{
    	// get private key to decrypt key file 
    	PrivateKey privateKey = (PrivateKey) getKey("mykey");

    	//set up Cipher to decrypt groupKeyFile 
    	Cipher deCipher = Cipher.getInstance(PUBKEY_ALGORITHM);
    	deCipher.init(Cipher.DECRYPT_MODE, privateKey);

    	//read and decrypt encoded group key from file  
    	byte[] groupKey = new byte[KEY_LENGTH];  
    	FileInputStream fis = new FileInputStream(groupKeyFile);
     
    	CipherInputStream cis = new CipherInputStream(fis, deCipher);
    	cis.read(groupKey);
    	
    	//add to key store
    	SecretKeySpec sks = new SecretKeySpec(groupKey, KEY_ALGORITHM);
        KeyStore.SecretKeyEntry skEntry = new KeyStore.SecretKeyEntry(sks);
        ks.setEntry(groupID, skEntry, new KeyStore.PasswordProtection(appPwdAsArray));

        //update stored copy of keystore  (can just rewrite as adding a new group is a rare occurrence)
        writeKeyStore();
    } 

    
    /****************************************************************************************************************
     * Lists all the groups in the keystore
     * @throws MissingPwdException
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public void listGroups() throws MissingPwdException, GeneralSecurityException, IOException {
    	Enumeration<String> es = ks.aliases();
    	for (String key : Collections.list(es)){
    		System.out.println(key + "found");
    	}
    } //end listGroups

    //**********************************************************************************************************************************************
    
    
    /****************************************************************************************************************
     * generates a new SecretKeySpec using the specified key algorithm
     */
    private SecretKeySpec genSKS() throws NoSuchAlgorithmException{ 
    KeyGenerator myKeyGenerator = KeyGenerator.getInstance(KEY_ALGORITHM);
    myKeyGenerator.init(KEY_LENGTH);
    return new SecretKeySpec(myKeyGenerator.generateKey().getEncoded(), KEY_ALGORITHM);
    }
    
    /****************************************************************************************************************
     * retrieves a key from the keystore
     * @param alias of the key to be retrieved
     */
    private Key getKey( String alias)  {
    	try {
    		if (ks.containsAlias(alias)){
    			Key key = ks.getKey(alias, appPwdAsArray);
    			return key;
    		}
    	} catch (GeneralSecurityException e) {
    		System.out.println( e.getMessage());
    	}

    	return null;  //ie not able to recover key
    } //end getKey


    /****************************************************************************************************************
     * Loads the key store from disc
     * @return
     * @throws IOException
     * @throws GeneralSecurityException
     */
    private KeyStore loadKeyStore() throws IOException, GeneralSecurityException {
    	FileInputStream fis = null;
    	try {
    		fis = new FileInputStream(KEYSTORE_NAME);
    		ks.load(fis, appPwdAsArray);
    	} catch (FileNotFoundException e) {
    		System.out.println("New keystore created");
    		//this should only get run if file store doesn't exists at all - creates a new one
    		ks.load(null);
    	} finally {
    		if (fis != null) fis.close();
    	}
    	return ks;
    } //end loadKeyStore
    
    
    /****************************************************************************************************************
     * Updates the stored copy of the keystore
     * @throws GeneralSecurityException
     * @throws IOException
     */
    private void writeKeyStore() throws GeneralSecurityException, IOException {
        FileOutputStream fos = new FileOutputStream(KEYSTORE_NAME);
        try {
            ks.store(fos, appPwdAsArray);
        } finally {
            fos.close();
        }
    } //end writeKeyStore
    
    /************************************************************************************************************************
     * Encrypts the group key with a public key and stores the result in a file
     * @param newSecretKeySpec
     * @param out
     * @param certFile
     * @throws IOException
     * @throws GeneralSecurityException
     */
    private void encryptWithPublicKey(SecretKeySpec sks, File out, String certFile) throws IOException, GeneralSecurityException {
    	// setup Cipher to do RSA encryption with public key
    	Cipher enCipher = Cipher.getInstance(PUBKEY_ALGORITHM);
    	
    	//read in the certificate from file
    	FileInputStream certIn = new FileInputStream(certFile);
    	CertificateFactory cf = CertificateFactory.getInstance("X.509");
    	X509Certificate cert = (X509Certificate)cf.generateCertificate(certIn);
    	
    	//extract public key from certificate
    	PublicKey pk = cert.getPublicKey();
    	
    	// encrypt group key and write to file
    	byte[] groupKey = sks.getEncoded();
    	enCipher.init(Cipher.ENCRYPT_MODE, pk);
    	CipherOutputStream os = new CipherOutputStream(new FileOutputStream(out), enCipher);
    	os.write(groupKey);
    	os.close();
    }
   
    
    /**********************************************************************************************************************8
     * validates that password (appPwd) provides access to the keystore
     * @param appPwd
     * @return
     * @throws IOException
     */
    protected boolean  validate() throws IOException{
        try {
            KeyStore ks = loadKeyStore();
            return true;
        } catch (GeneralSecurityException e) {
            return false;
        }
    }

} //end AppKeystore




