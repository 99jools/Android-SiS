
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.Certificate;
import java.util.Collections;
import java.util.Enumeration;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * Created by jrs300 on 01/08/14.
 */

public class AppKeystore {

    public static final String KEY_ALGORITHM = "AES";
    public static final String KEYPAIR_ALGORITHM = "RSA";
    public static final String PROVIDER = "BC";
    public static final int KEY_LENGTH = 128;
    public static final int KEYPAIR_LENGTH = 2048;
    public static final String KEYSTORE_NAME = "/media/348C-C649/admin/SiSKeyStore.jceks";
    public static final String CERTIFICATE_FILE = "/media/348C-C649/admin/SiSCert.jceks";
    
    
 //   public static final String KEYSTORE_NAME = "/media/348C-C649/jrsmaster/SiSKeyStore.jceks";
 //   public static final String CERTIFICATE_FILE = "/media/348C-C649/jrsmaster/SiSCert.jceks";
    
    public static final String KEYSTORE_TYPE = "JCEKS";
 
    
    private char[] appPwdAsArray;
    private KeyStore ks;
    private KeyStore cs;

    public AppKeystore() throws MissingPwdException{
    	String appPwd = AppPwdObj.getInstance().getValue();
    	this.appPwdAsArray = appPwd.toCharArray();
    	try {
    		this.ks = loadKeyStore(appPwdAsArray);
    		this.cs = loadCertStore(appPwdAsArray);
    		System.out.println(cs.size());
    		writeKeyStore();
    	} catch (IOException e) {
    		System.out.println( e.getMessage());
    	} catch (GeneralSecurityException e) {
    		System.out.println( e.getMessage());
    	}

    }
    
/**
 * Retrieves the key from the Keystore corresponding to the supplied alias and wraps it as SecretKeySpec
 * @param alias
 * @return KeySpec for the retrieved key
 */
    public SecretKeySpec getSKS(String alias){
    	
    	try {
			if (ks.containsAlias(alias)){
				Key key = ks.getKey(alias, appPwdAsArray);
				return new SecretKeySpec(key.getEncoded(), KEY_ALGORITHM);
			} 
		} catch (UnrecoverableKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
    }
  
        
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
    
    
    /****************************************************************************************************************
     * Loads the key store from disc
     * @return
     * @throws IOException
     * @throws GeneralSecurityException
     */
    private static KeyStore loadKeyStore(char[] pwd) throws IOException, GeneralSecurityException {
        KeyStore myks = KeyStore.getInstance(KEYSTORE_TYPE);
        FileInputStream fis = null;
        try {
        	fis = new FileInputStream(KEYSTORE_NAME);
            myks.load(fis, pwd);
        } catch (FileNotFoundException e) {
           myks.load(null);

        } finally {
            if (fis != null) fis.close();
        }
        return myks;
    } //end loadKeyStore
    

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


    
 //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Stuff that needs the Certificate Store    
    
    
    /****************************************************************************************************************
     * Loads the certificate store from disc
     * @return
     * @throws IOException
     * @throws GeneralSecurityException
     */
    private static KeyStore loadCertStore(char[] pwd) throws IOException, GeneralSecurityException {
        KeyStore myCertStore = KeyStore.getInstance(KEYSTORE_TYPE);
        FileInputStream fis = null;
        try {
        	File file = new File(CERTIFICATE_FILE);
        	fis = new FileInputStream(file);
        	 myCertStore.load(fis, pwd);
        	
        } catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
            
        } finally {
            if (fis != null) fis.close();
        }
        return myCertStore;
    } //end loadKeyStore
    /**
     * 
     * 
     * 
     * THIS CODE IS ONLY APPLICABLE TO JAVA VERSION - does not apply to Android version
     * 
     * Generates a new group encryption key for AES encryption using 256 bit key and stores in KeyStore
     * @param groupID
         * @return
     * @throws MissingPwdException
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public SecretKeySpec newGroupKey(String groupID, String savePath ) 
            throws MissingPwdException,IOException, GeneralSecurityException{
    
    	if (ks.containsAlias(groupID)) return null;
    

        // otherwise generate a new key
        SecretKeySpec sks = genSKS();
        
        
    	
        FileOutputStream fospk = new FileOutputStream(new File(savePath+ "SymmetricKey.sym"));          	
    	// encrypt group key and write to file
    	byte[] groupKey = sks.getEncoded();
    
    	fospk.write(groupKey);
    	fospk.close();        	
                	
                	
                	
                	
                	
        
        /*for each certificate in Certificate Keystore, encrypt with public key contained in certificate
         * and write out to path specified with filename of  alias.xeb
         * 
         */
        Enumeration<String> users = cs.aliases();
        for (String user : Collections.list(users)){
        	System.out.println("Key generated for " + user);
        	
          	//encrypt key and write out encrypted file 
        	encryptWithPublicKey(sks, user, savePath);
        }
        
        	//add to encryption key store - NOTE THIS COULD BE REMOVED IF REQUIRED TO KEEP SECRET FROM ADMIN
        	KeyStore.SecretKeyEntry skEntry = new KeyStore.SecretKeyEntry(sks);
        	ks.setEntry(groupID, skEntry, new KeyStore.PasswordProtection(appPwdAsArray));

        	//update stored copy of keystore  (can just rewrite as adding a new group is a rare occurrence)
        	writeKeyStore();

        	return sks;
        	
        } //end addGroupKey

        /************************************************************************************************************************
         * Encrypts the group key with a public key and stores the result in a file
         * @param newSecretKeySpec
         * @param out
         * @param certFile
         * @throws IOException
         * @throws GeneralSecurityException
         */
        protected void encryptWithPublicKey(SecretKeySpec sks, String user, String savePath) throws IOException, GeneralSecurityException {
        	// setup Cipher to do RSA encryption with public key
        	 Security.addProvider(new BouncyCastleProvider());
        	Cipher enCipher = Cipher.getInstance(KEYPAIR_ALGORITHM, PROVIDER);
        	
        	//create fileoutputstream
        	FileOutputStream fos = new FileOutputStream(new File(savePath+ user +".xeb"));
     	
        	//extract public key from certificate
        	Certificate cert = cs.getCertificate(user);
        	PublicKey pk = cert.getPublicKey();
          	
        	// encrypt group key and write to file
        	byte[] groupKey = sks.getEncoded();
        	
        	
        	
        	
        	
        	enCipher.init(Cipher.ENCRYPT_MODE, pk);
        	
        	
        	
        	
        	CipherOutputStream os = new CipherOutputStream(fos, enCipher);
        	os.write(groupKey);
        	os.close();
        }
  
        /**
         * Gets the private key - used for signing key files
         * @return the private key
         */
        private PrivateKey getMyPrivateKey(){
        	PrivateKey key = null;
        	try {
        		System.out.println(cs.isKeyEntry("rsassokey"));
        		key = (PrivateKey) cs.getKey("rsassokey", "fred".toCharArray());
    //     		key = (PrivateKey) cs.getKey("rsassokey", appPwdAsArray);
        	} catch (UnrecoverableKeyException e) {
        		// TODO Auto-generated catch block
        		e.printStackTrace();
        	} catch (KeyStoreException e) {
        		// TODO Auto-generated catch block
        		e.printStackTrace();
        	} catch (NoSuchAlgorithmException e) {
        		// TODO Auto-generated catch block
        		e.printStackTrace();
        	}
        	return key;
        }
    
        /**
         * with reference to code from http://www.macs.hw.ac.uk/~ml355/lore/pkencryption.htm 
         */
        public void importGroupKey(String groupID, File groupKeyFile) throws GeneralSecurityException, IOException{
        	// get private key to decrypt key file 
        	PrivateKey privateKey = getMyPrivateKey();

 //       	PrintPrivateKey.printPK(cs);
        	
        	//set up Cipher to decrypt groupKeyFile 
        	 Security.addProvider(new BouncyCastleProvider());
        	Cipher deCipher = Cipher.getInstance(KEYPAIR_ALGORITHM, PROVIDER);
        	deCipher.init(Cipher.DECRYPT_MODE, privateKey);

        	//read and decrypt encoded group key from file  
        	byte[] groupKey = new byte[KEY_LENGTH/8];   //need to convert to bytes
        	FileInputStream fis = new FileInputStream(groupKeyFile);
//PrintPrivateKey.printEncrypted(fis);        



        	CipherInputStream cis = new CipherInputStream(fis, deCipher);
        	int g = cis.read(groupKey);
        	
        	//add to key store
        	SecretKeySpec sks = new SecretKeySpec(groupKey, KEY_ALGORITHM);
            KeyStore.SecretKeyEntry skEntry = new KeyStore.SecretKeyEntry(sks);
            ks.setEntry(groupID, skEntry, new KeyStore.PasswordProtection(appPwdAsArray));

            
            
            PrintPrivateKey.printSymK(groupKey);
            
            
            
            //update stored copy of keystore  (can just rewrite as adding a new group is a rare occurrence)
            writeKeyStore();
       
        } 
    
        /****************************************************************************************************************
         * generates a new SecretKeySpec using the specified key algorithm
         */
        private SecretKeySpec genSKS() throws NoSuchAlgorithmException{ 
        KeyGenerator myKeyGenerator = KeyGenerator.getInstance(KEY_ALGORITHM);
        myKeyGenerator.init(KEY_LENGTH);
        return new SecretKeySpec(myKeyGenerator.generateKey().getEncoded(), KEY_ALGORITHM);
        }
    
   
} //end AppKeystore





/* ******************************************************************************************************************************************************
 *private PublicKey getPublicKey(File certFile){
  	
	//read in the certificate from file
	FileInputStream certIn;
	PublicKey pk = null;
	try {
		certIn = new FileInputStream(certFile);
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		X509Certificate cert = (X509Certificate) cf.generateCertificate(certIn);
		pk = cert.getPublicKey();
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (CertificateException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return pk;
}


public void testEnc(String s, File certFile, File outFile) throws IOException, GeneralSecurityException {
	// setup Cipher to do RSA encryption with public key
	Cipher enCipher = Cipher.getInstance(KEYPAIR_ALGORITHM);
	
	PublicKey pk = getPublicKey(certFile);
  	
	// encrypt group key and write to file
	byte[] stuff = s.getBytes("UTF-8");
	enCipher.init(Cipher.ENCRYPT_MODE, pk);
	CipherOutputStream os = new CipherOutputStream(new FileOutputStream(outFile), enCipher);
	os.write(stuff);
	os.close();
}

public void testDec(File encFile, File outFile) throws IOException, GeneralSecurityException {
	// setup Cipher to do RSA encryption 
	Cipher deCipher = Cipher.getInstance(KEYPAIR_ALGORITHM);
	
	PrivateKey pk = getPrivateKey();

	deCipher.init(Cipher.DECRYPT_MODE, pk);
	FileOutputStream fos = new FileOutputStream(outFile);
	CipherInputStream cis = new CipherInputStream(new FileInputStream(encFile), deCipher);


    //read and decrypt file
    byte[] block = new byte[KEYPAIR_LENGTH];
    int bytesRead;
    bytesRead = cis.read(block);
    while (bytesRead != -1) {
        fos.write(block,0,bytesRead);
        bytesRead = cis.read(block);  //read next block
    }
    //close file
    fos.close();
    cis.close();

}

    /****************************************************************************************************************
     * retrieves a key from the keystore and writes it out to file - for testing purposes only
     * @param alias of the key to be retrieved

    private Key getKey( String alias)  {
    	try {
    		if (ks.containsAlias(alias)){
       			Key key = ks.getKey(alias, appPwdAsArray);
   	        FileOutputStream temp = new FileOutputStream(new File("/home/students/jrs300/AndroidStudioProjects/workspaceP/shareinsecrettest/secretkeyforjulie"));
  	        temp.write(key.getEncoded());
	        temp.close();
       			return key;
    		}
    	} catch (GeneralSecurityException e) {
    		e.printStackTrace();
    		
		}

    	return null;  //ie not able to recover key
    } //end getKey
 */


