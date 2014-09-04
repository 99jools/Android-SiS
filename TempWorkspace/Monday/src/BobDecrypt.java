import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;


public class BobDecrypt {
	public static void main(String[] args) {
		AppKeystore aks;
				
		// initialise AppPwdObj
			AppPwdObj apo = AppPwdObj.makeObj();
	
		try {
			apo.setValue("bob");
			aks = new AppKeystore();
		
			
		FileCryptor.decryptFile(new FileInputStream("/home/students/jrs300/Dropbox/SecurelyShare/demo/TaleofTwoCities.txt.xps"),	
				new FileOutputStream("/media/348C-C649/Sample data/TaleofTwoCities.decrypted.txt"));		
		FileCryptor.decryptFile(new FileInputStream("/home/students/jrs300/Dropbox/SecurelyShare/demo/dream-speech.pdf.xps"),	
				new FileOutputStream("/media/348C-C649/Sample data/dream-speech.decrypted.pdf"));
		
		
		} catch (MissingPwdException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		}

}

