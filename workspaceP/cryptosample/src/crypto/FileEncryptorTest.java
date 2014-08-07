package crypto;
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
SharedPrefs prefs;
	
	@Test
	public void testEncryptFile() throws IOException, GeneralSecurityException {
		
	    fis = new FileInputStream("/home/students/jrs300/a.txt");
	    fos = new FileOutputStream("/home/students/jrs300/aaa.txt.xps");
	    fisd = new FileInputStream("/home/students/jrs300/aaa.txt.xps");
	    fosd = new FileOutputStream("/home/students/jrs300/aaa.decrypt.txt");

	    // initialise AppPwdObj
	    AppPwdObj appPwd = AppPwdObj.getInstance();
	    appPwd.setValue("password");
	    
	    //get SharedPrefs
	    prefs = new SharedPrefs("SharedPrefs.csv");
	    
	    //add some groups and generate corresponding keys
	    addGroup("Mygroup");
	    addGroup("Newgroup");
	    addGroup("Mygroup");
	    addGroup("Julie");
	    
	    FileCryptor.encryptFile(fis, fos, "Newgroup", prefs);
	      
	    decryptFor = FileCryptor.decryptFile(fisd, fosd, prefs);  
	    System.out.println(decryptFor);
		
		String newString = "IT WAS the best of times, it was the worst of times, it was the age of wisdom, it was the age of foolishness, it was the epoch of belief";
	  
	    String myString = "In the beginning was the Word, and the Word was with God, and the Word was God."
	    	+ "He was with God in the beginning. Through him all things were made; without him nothing was made that has been made."
	    	+ "In him was life, and that life was the light of all mankind. "
	    	+" The light shines in the darkness, and the darkness has not overcome it.";
	    FileOutputStream sosS = new FileOutputStream("/home/students/jrs300/s.enc");
	    
	    FileCryptor.encryptString(myString, sosS, "Mygroup",prefs);
	    
	    sisd = new FileInputStream("/home/students/jrs300/s.xps");
	    sosd = new FileOutputStream("/home/students/jrs300/s.txt");
	
	    decryptFor = FileCryptor.decryptFile(sisd, sosd, prefs);  
	    System.out.println(decryptFor);
		}
	
	private void addGroup(String groupID){
		if ( !prefs.codeExists(groupID))
			try {
				AppKeystore.addGroupKey(groupID, prefs);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (GeneralSecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

}
