

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
//			aks.newGroupKey("MondayOne", "/home/students/jrs300/Dropbox/SecurelyShare/MondayOne/Encryption_Keys/");
			
			
			FileCryptor.encryptFile(new FileInputStream("/media/348C-C649/Beginning.txt"),
						new FileOutputStream("/home/students/jrs300/Dropbox/SecurelyShare/MondayOne/Beginning.txt.xps"), "MondayOne");
			
			
			
		} catch (MissingPwdException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		
		}
		
		

	}

}
