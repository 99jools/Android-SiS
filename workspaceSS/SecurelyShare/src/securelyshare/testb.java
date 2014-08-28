package securelyshare;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import javax.crypto.spec.SecretKeySpec;
import org.junit.Test;


public class testb {
		AppKeystore aks;
		
		@Test
		public void testEncryptFile() throws IOException, GeneralSecurityException, MissingPwdException {

			// initialise AppPwdObj
			AppPwdObj apo = AppPwdObj.makeObj();
			apo.setValue("password");
			aks = new AppKeystore();
			
//			SecretKeySpec insks = 	aks.addGroupKey("julie", new File("/home/students/jrs300/AndroidStudioProjects/workspaceP/shareinsecrettest/rsacert.cert"), 	new File("/home/students/jrs300/AndroidStudioProjects/workspaceP/shareinsecrettest/rsaencryptedAESkey.enc"));
//			FileCryptor.encryptFile(new FileInputStream("/home/students/jrs300/dream-speech.pdf"), new FileOutputStream("/home/students/jrs300/Dropbox/ShareInSecret/test/dream-speech.pdf.xps"), "julie");
			FileCryptor.decryptFile(new FileInputStream("/home/students/jrs300/Dropbox/ShareInSecret/test/dream-speech.pdf.xps"), new FileOutputStream("/home/students/jrs300/AndroidStudioProjects/workspaceP/shareinsecrettest/decrypted.pdf"));
//			SecretKeySpec sks = aks.importGroupKey("julie", new File("/home/students/jrs300/AndroidStudioProjects/workspaceP/shareinsecrettest/rsaencryptedAESkey.enc"));
			
			
		
		
		}
}



/*

aks.testEnc("somewhat longer word test with additional padding", new File("/home/students/jrs300/AndroidStudioProjects/workspaceP/shareinsecrettest/rsacert.cert"),	new File("/home/students/jrs300/AndroidStudioProjects/workspaceP/shareinsecrettest/rsaencrypted.enc") );
aks.testDec( new File("/home/students/jrs300/AndroidStudioProjects/workspaceP/shareinsecrettest/rsaencryptedAESkey.enc"),new File("/home/students/jrs300/AndroidStudioProjects/workspaceP/shareinsecrettest/decryptedAESkey") );
	
			SecretKeySpec insks = 	aks.addGroupKey("julie", new File("/home/students/jrs300/AndroidStudioProjects/workspaceP/shareinsecrettest/rsacert.cert"), 	new File("/home/students/jrs300/AndroidStudioProjects/workspaceP/shareinsecrettest/rsaencryptedAESkey.enc"));
	
						SecretKeySpec sks = aks.getSKS("julie");
						
									SecretKeySpec sks = aks.importGroupKey("julie", new File("/home/students/jrs300/AndroidStudioProjects/workspaceP/shareinsecrettest/rsaencryptedAESkey.enc"));
*/