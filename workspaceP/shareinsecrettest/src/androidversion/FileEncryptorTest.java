package androidversion;
import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import org.junit.Test;

public class FileEncryptorTest {
FileInputStream fis;
FileOutputStream fos;
FileInputStream fisd;
FileOutputStream fosd;
FileInputStream sisd;
FileOutputStream sosd;
String decryptFor;

	
	@Test
	public void testEncryptFile() throws IOException, GeneralSecurityException {
		
	    fis = new FileInputStream("/home/students/jrs300/a.txt");
	    fos = new FileOutputStream("/home/students/jrs300/Dropbox/ShareInSecret/My test group/aaa.txt.xps");
	    fisd = new FileInputStream("/home/students/jrs300/aaa.txt.xps");
	    fosd = new FileOutputStream("/home/students/jrs300/Dropbox/ShareInSecret/My test group/aaa.decrypt.txt");

	    // initialise AppPwdObj
	    AppPwdObj apo = AppPwdObj.makeObj();
	    apo.setValue("password");
	    
	    
	    //add some groups and generate corresponding keys
//	    addGroup("My test group");
//	    addGroup("groupZ");
//	    addGroup("groupZ");
	    
	    try {
			FileCryptor.encryptFile(fis, fos, "My test group");
		} catch (MissingPwdException e) {
			System.out.println(e.getMessage());
		}
      
	    try {
			decryptFor = FileCryptor.decryptFile(fisd, fosd);
		} catch (MissingPwdException e) {
			System.out.println(e.getMessage());
		}  
	
		String newString = "IT WAS the best of times, it was the worst of times, it was the age of wisdom, it was the age of foolishness, it was the epoch of belief";
	  
	    String myString = "In the beginning was the Word, and the Word was with God, and the Word was God."
	    	+ "He was with God in the beginning. Through him all things were made; without him nothing was made that has been made."
	    	+ "In him was life, and that life was the light of all mankind. "
	    	+" The light shines in the darkness, and the darkness has not overcome it.";
	    FileOutputStream sosS = new FileOutputStream("/home/students/jrs300/Dropbox/ShareInSecret/My test group/s.enc");
	    
	    try {
			FileCryptor.encryptString(myString, sosS, "My test group");
		} catch (MissingPwdException e) {
			System.out.println(e.getMessage());
		}
	    
	    sisd = new FileInputStream("/home/students/jrs300/s.xps");
	    sosd = new FileOutputStream("/home/students/jrs300/Dropbox/ShareInSecret/My test group/s.txt");
	
	    try {
			decryptFor = FileCryptor.decryptFile(sisd, sosd);
		} catch (MissingPwdException e) {
			System.out.println(e.getMessage());
		}  

	}	
	private void addGroup(String groupID){
				try {
				AppKeystore.addGroupKey(groupID);
			} catch (IOException e) {
				System.out.println(e.getMessage());
			} catch (GeneralSecurityException e) {
				System.out.println(e.getMessage());
			} catch (MissingPwdException e) {
				System.out.println(e.getMessage());
			}
	}

}
