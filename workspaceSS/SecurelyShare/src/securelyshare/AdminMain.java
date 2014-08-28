package securelyshare;

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
			aks.newGroupKey("GroupZ", "/home/students/jrs300/Dropbox/SecurelyShare/GroupZ/Encryption_Keys/");
			
		} catch (MissingPwdException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

	}

}
