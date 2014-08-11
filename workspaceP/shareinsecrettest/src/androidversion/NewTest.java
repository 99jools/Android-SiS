package androidversion;
import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import org.junit.Test;

public class NewTest {

	FileInputStream fis;
	FileOutputStream fos;

	String decryptFor;

		
		@Test
		public void testEncryptFile() throws IOException, GeneralSecurityException {
			
			 // initialise AppPwdObj
		    AppPwdObj apo = AppPwdObj.makeObj();
		    apo.setValue("password");
		    
		    
		    fis = new FileInputStream("/home/students/jrs300/dream-speech.pdf");
	    fos = new FileOutputStream("/home/students/jrs300/Dropbox/ShareInSecret/My test group/dream-speech.pdf.xps");
	    try {
			FileCryptor.encryptFile(fis, fos, "My test group");
		} catch (MissingPwdException e) {
			System.out.println(e.getMessage());
		}
		
			fis = new FileInputStream("/home/students/jrs300/Dropbox/ShareInSecret/My test group/dream-speech.pdf.xps");
	        fos =    new FileOutputStream("/home/students/jrs300//Dropbox/ShareInSecret/My test group/my decrypted file.pdf");
		   
		    
		    
		    try {
				FileCryptor.decryptFile(fis, fos);
			} catch (MissingPwdException e) {
				System.out.println(e.getMessage());
			}

		
		 String myString = "In the beginning was the Word, and the Word was with God, and the Word was God."
		    	+ "He was with God in the beginning. Through him all things were made; without him nothing was made that has been made."
		    	+ "In him was life, and that life was the light of all mankind. "
		    	+" The light shines in the darkness, and the darkness has not overcome it.";

		    
		    try {
		    	FileOutputStream sosS = new FileOutputStream("/home/students/jrs300/Dropbox/ShareInSecret/My test group/string encrypt.xps");
		     	FileCryptor.encryptString(myString, sosS, "My test group");
			} catch (MissingPwdException e) {
				System.out.println(e.getMessage());
			}
		    
		    fis = new FileInputStream("/home/students/jrs300/Dropbox/ShareInSecret/My test group/string encrypt.xps");
		    fos = new FileOutputStream("/home/students/jrs300/Dropbox/ShareInSecret/My test group/s.txt");
		
		    try {
				decryptFor = FileCryptor.decryptFile(fis, fos);
			} catch (MissingPwdException e) {
				System.out.println(e.getMessage());
			}  

		}
}
	 