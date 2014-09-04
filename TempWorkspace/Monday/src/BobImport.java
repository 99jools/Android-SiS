import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.crypto.spec.SecretKeySpec;


public class BobImport {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		AppKeystore aks;
				
		// initialise AppPwdObj
			AppPwdObj apo = AppPwdObj.makeObj();
	
		try {
			apo.setValue("bob");
			aks = new AppKeystore();
			aks.importGroupKey("demo", new File("/home/students/jrs300/Dropbox/SecurelyShare/demo/Encryption_Keys/bob.xeb"));
			
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
