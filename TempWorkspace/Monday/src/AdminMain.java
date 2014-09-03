

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class AdminMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		AppKeystore aks;
				
		// initialise AppPwdObj
			AppPwdObj apo = AppPwdObj.makeObj();
	
		try {
			apo.setValue("admin");
			aks = new AppKeystore();
		Boolean success = new File("/home/students/jrs300/Dropbox/SecurelyShare/GroupM/Encryption_Keys").mkdirs();

			aks.newGroupKey("GroupM", "/home/students/jrs300/Dropbox/SecurelyShare/GroupM/Encryption_Keys/");
			
			
		FileCryptor.encryptFile(new FileInputStream("/media/348C-C649/Beginning.txt"),	new FileOutputStream("/home/students/jrs300/Dropbox/SecurelyShare/GroupM/Beginning.txt.xps"), "GroupM");
		FileCryptor.encryptFile(new FileInputStream("/media/348C-C649/TaleofTwoCities.txt"),	new FileOutputStream("/home/students/jrs300/Dropbox/SecurelyShare/GroupM/TaleofTwoCities.txt.xps"), "GroupM");		
			
			
		} catch (MissingPwdException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 		}
		
		

	}

