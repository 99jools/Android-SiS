

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
import javax.crypto.spec.SecretKeySpec;

public class KeyManagement {
	public static final String KEY_ALGORITHM = "AES";
	public static final String KEYSTORE_NAME = "SiSKeyStore";
	private KeyStore ks;

//constructor
	public KeyManagement(String password) throws GeneralSecurityException, IOException {
        this.ks = loadKeyStore(KEYSTORE_NAME, password);
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
	}


	/**
	 * generateKey method called to generate a new encryption key for AES encryption using 256 bit key
	 * @throws GeneralSecurityException 
	 */
	public SecretKeySpec createNewKey() throws GeneralSecurityException{
		KeyGenerator myKeyGenerator = KeyGenerator.getInstance(KEY_ALGORITHM);
		myKeyGenerator.init(256);
		return new SecretKeySpec(myKeyGenerator.generateKey().getEncoded(), KEY_ALGORITHM);
	}

	public SecretKeySpec dummyKey(){
		//set up dummy key - CHANGE THIS
		byte[] dummykey ={0x00, 0x11, 0x11, 0x11, 0x00, 0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x01, 0x11,0x11, 0x11, 0x11};
		return new SecretKeySpec(dummykey, KEY_ALGORITHM);

	}

public void writeKeyStore(KeyStore ks, String ksName, char[] passArray) throws GeneralSecurityException, NoSuchAlgorithmException, CertificateException, IOException {
	
	// store away the keystore
	System.out.println("in writekeystore");
    FileOutputStream fos = null;
    try {
        fos = new FileOutputStream(ksName);
        System.out.println(fos.getFD().toString());
        ks.store(fos, passArray);
    } finally {
        if (fos != null) {
            fos.close();
        }
    }
}

public KeyStore loadKeyStore(String ksName, String password) 
throws IOException, GeneralSecurityException {
KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
java.io.FileInputStream fis = null;

try {
	fis = new java.io.FileInputStream(ksName);
	ks.load(fis, password.toCharArray());
} catch (FileNotFoundException e) {
	System.out.println("FileNotFound caught in catch block" + e);
	
	//this should only get run if file store doesn't exists at all
	ks.load(null);
	writeKeyStore(ks, ksName, password.toCharArray());
} finally {
	if (fis != null) {
	fis.close();
	System.out.println("file close in finally block");
	} 
}
return ks;
}

}
