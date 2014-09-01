
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
			apo.setValue("admin");
			aks = new AppKeystore();
			
			
			FileCryptor.encryptFile(new FileInputStream("I:Beginning.txt"),
						new FileOutputStream("I:Grouptest/Beginning.txt.xps"), "Grouptest");
			
		
		}
		
//		FileCryptor.decryptFile(new FileInputStream("/home/students/jrs300/Dropbox/ShareInSecret/test/dream-speech.pdf.xps"), new FileOutputStream("/home/students/jrs300/AndroidStudioProjects/workspaceP/shareinsecrettest/decrypted.pdf"));
//			SecretKeySpec sks = aks.importGroupKey("julie", new File("/home/students/jrs300/AndroidStudioProjects/workspaceP/shareinsecrettest/rsaencryptedAESkey.enc"));
			
			@Test
			public void testimportKey(){
				// initialise AppPwdObj
				AppPwdObj apo = AppPwdObj.makeObj();
				try {
					apo.setValue("admin");	
					aks = new AppKeystore();
					aks.importGroupKey("Grouptest", new File("I:Grouptest/Encryption_Keys/jrstest.xeb"));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (GeneralSecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (MissingPwdException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
				
				
				

			}
		

}



/*

aks.testEnc("somewhat longer word test with additional padding", new File("/home/students/jrs300/AndroidStudioProjects/workspaceP/shareinsecrettest/rsacert.cert"),	new File("/home/students/jrs300/AndroidStudioProjects/workspaceP/shareinsecrettest/rsaencrypted.enc") );
aks.testDec( new File("/home/students/jrs300/AndroidStudioProjects/workspaceP/shareinsecrettest/rsaencryptedAESkey.enc"),new File("/home/students/jrs300/AndroidStudioProjects/workspaceP/shareinsecrettest/decryptedAESkey") );
	
			SecretKeySpec insks = 	aks.addGroupKey("julie", new File("/home/students/jrs300/AndroidStudioProjects/workspaceP/shareinsecrettest/rsacert.cert"), 	new File("/home/students/jrs300/AndroidStudioProjects/workspaceP/shareinsecrettest/rsaencryptedAESkey.enc"));
	
						SecretKeySpec sks = aks.getSKS("julie");
						
									SecretKeySpec sks = aks.importGroupKey("julie", new File("/home/students/jrs300/AndroidStudioProjects/workspaceP/shareinsecrettest/rsaencryptedAESkey.enc"));
*/