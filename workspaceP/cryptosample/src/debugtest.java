	import java.io.FileInputStream;
	import java.io.FileOutputStream;
	import java.io.IOException;
	import java.security.InvalidAlgorithmParameterException;
	import java.security.InvalidKeyException;
	import java.security.NoSuchAlgorithmException;

	import javax.crypto.BadPaddingException;
	import javax.crypto.IllegalBlockSizeException;
	import javax.crypto.NoSuchPaddingException;

	import org.junit.Test;

public class debugtest {

	FileInputStream fisd;
	FileOutputStream fosd;

		
		@Test
		public void testEncryptFile() throws InvalidKeyException, NoSuchAlgorithmException, 
		   NoSuchPaddingException, IOException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

		    KeyManagement returnKeyMgt = new KeyManagement();
		    fisd = new FileInputStream("/home/students/jrs300/aaa.txt.enc");
		    fosd = new FileOutputStream("/home/students/jrs300/aaa.decrypt.txt");
		    
		    FileEncryptor.decryptFile(fisd, fosd, returnKeyMgt);
		    
		}}    


