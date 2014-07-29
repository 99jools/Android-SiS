	import java.io.FileInputStream;
	import java.io.FileOutputStream;
	import java.io.IOException;
import java.security.GeneralSecurityException;
	import java.security.InvalidAlgorithmParameterException;
	import java.security.InvalidKeyException;
import java.security.KeyStoreException;
	import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

	import javax.crypto.BadPaddingException;
	import javax.crypto.IllegalBlockSizeException;
	import javax.crypto.NoSuchPaddingException;

import org.junit.Test;

public class debugtest {

	FileInputStream fisd;
	FileOutputStream fosd;

		
		@Test
		public void testEncryptFile()  {
			try {
		    KeyManagement km = new KeyManagement("password");
	//	    km.createNewKey("mygroup", "julie");
	//	    km.createNewKey("newGroup", "password");
		    
		    System.out.println(km.getExistingKey("newGroup", "password").getAlgorithm());
} catch (KeyStoreException e) {
	System.out.println("Exception thrown " + e);
} catch (NoSuchAlgorithmException e) {
	System.out.println("Exception thrown " + e);
} catch (CertificateException e) {
	System.out.println("Exception thrown " + e);
} catch (IOException e) {
	System.out.println("Exception thrown " + e);
} catch (GeneralSecurityException e) {
	System.out.println("Exception thrown " + e);
} 
		}}    


