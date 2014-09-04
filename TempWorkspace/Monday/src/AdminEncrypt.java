import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;


public class AdminEncrypt {
	public static void main(String[] args) {
		AppKeystore aks;
				
		// initialise AppPwdObj
			AppPwdObj apo = AppPwdObj.makeObj();
	
		try {
			apo.setValue("admin");
			aks = new AppKeystore();
		
		FileCryptor.encryptFile(new FileInputStream("/media/348C-C649/Sample data/Beginning.txt"),	
				new FileOutputStream("/home/students/jrs300/Dropbox/SecurelyShare/anothergroup/Beginning.txt.xps"), "anothergroup");
		FileCryptor.encryptFile(new FileInputStream("/media/348C-C649/Sample data/asimov.pdf"),	
				new FileOutputStream("/home/students/jrs300/Dropbox/SecurelyShare/anothergroup/asimov.pdf.xps"), "anothergroup");	
		
		
		FileCryptor.encryptFile(new FileInputStream("/media/348C-C649/Sample data/TaleofTwoCities.txt"),	
				new FileOutputStream("/home/students/jrs300/Dropbox/SecurelyShare/demo/TaleofTwoCities.txt.xps"), "demo");		
		FileCryptor.encryptFile(new FileInputStream("/media/348C-C649/Sample data/dream-speech.pdf"),	
				new FileOutputStream("/home/students/jrs300/Dropbox/SecurelyShare/demo/dream-speech.pdf.xps"), "demo");
		
		
		} catch (MissingPwdException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		
		}		}
		
		

}
