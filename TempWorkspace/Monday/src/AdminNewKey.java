

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class AdminNewKey {

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

			aks.newGroupKey("demo", "/home/students/jrs300/Dropbox/SecurelyShare/demo/Encryption_Keys/");
//			aks.newGroupKey("anothergroup", "/home/students/jrs300/Dropbox/SecurelyShare/anothergroup/Encryption_Keys/");
		
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

