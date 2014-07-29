

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class KeyManagement {
	public static final String KEY_ALGORITHM = "AES";
	public static final String KEYSTORE_NAME = "SiSKeyStore";
	private KeyStore ks;

	//constructor
	public KeyManagement(String password) throws GeneralSecurityException, IOException {
		this.ks = loadKeyStore(password);
	}

	public SecretKeySpec getExistingKey(String distGroup) throws UnrecoverableKeyException {
		//check if key is found and recover if available

		try {
			if (this.ks.containsAlias(distGroup)){
				Key groupKey = this.ks.getKey(distGroup,null);
				return new SecretKeySpec(groupKey.getEncoded(), KEY_ALGORITHM);
			}

		} catch (KeyStoreException e) {
			// this should not happen as we have already checked for it
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// AES is a standard algorithm - OK to crash if not found
			e.printStackTrace();
		}
		return null;  //ie not able to recover key
	} //end getExistingKey


	/**
	 * generateKey method called to generate a new group encryption key for AES encryption using 256 bit key
	 * @throws GeneralSecurityException 
	 * @throws IOException 
	 */
	public SecretKeySpec createNewKey(String newGroup, String password) throws GeneralSecurityException, IOException{
		KeyGenerator myKeyGenerator = KeyGenerator.getInstance(KEY_ALGORITHM);
		myKeyGenerator.init(256);
		 SecretKeySpec newSecretKeySpec = 
	    		new SecretKeySpec(myKeyGenerator.generateKey().getEncoded(), KEY_ALGORITHM); 
		
		 //add new key to keystore
	    KeyStore.SecretKeyEntry skEntry = new KeyStore.SecretKeyEntry(newSecretKeySpec);
	    ks.setEntry(newGroup, skEntry, null);
	    
	    //write updated keystore to disk 
	    writeKeyStore(password.toCharArray());
			
		return newSecretKeySpec;
	} //end createNewKey

	public SecretKeySpec dummyKey(){
		//set up dummy key - CHANGE THIS
		byte[] dummykey ={0x00, 0x11, 0x11, 0x11, 0x00, 0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x01, 0x11,0x11, 0x11, 0x11};
		return new SecretKeySpec(dummykey, KEY_ALGORITHM);

	}

/**
 * Loads the App keystore using the supplied password
 * @param password
 * @return
 * @throws IOException
 * @throws GeneralSecurityException
 */
	public KeyStore loadKeyStore(String password) 
				throws IOException, GeneralSecurityException {
		KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
		java.io.FileInputStream fis = null;

		try {
			fis = new java.io.FileInputStream(KEYSTORE_NAME);
			ks.load(fis, password.toCharArray());
		} catch (FileNotFoundException e) {
			System.out.println("Keystore was not initialised - new one created");

			//this should only get run if file store doesn't exists at all
			ks.load(null);
			writeKeyStore(password.toCharArray());
		} finally {
			if (fis != null) {
				fis.close();
			} 
		}
		return ks;
	} //end loadKeyStore

	public void writeKeyStore( char[] passArray) throws GeneralSecurityException, NoSuchAlgorithmException, CertificateException, IOException {

		// write the updated keystore to disk
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(KEYSTORE_NAME);
			System.out.println(fos.getFD().toString());
			ks.store(fos, passArray);
		} finally {
			if (fos != null) {
				fos.close();
			}
		}
	} //end writeKeyStore
}
