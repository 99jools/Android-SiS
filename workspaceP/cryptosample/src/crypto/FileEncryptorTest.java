package crypto;
import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.junit.Test;



public class FileEncryptorTest {
FileInputStream fis;
FileOutputStream fos;
FileInputStream fisd;
FileOutputStream fosd;
String decryptFor;
	
	@Test
	public void testEncryptFile() throws IOException, GeneralSecurityException {
		
	    fis = new FileInputStream("/home/students/jrs300/a.txt");
	    fos = new FileOutputStream("/home/students/jrs300/aaa.txt.enc");
	    fisd = new FileInputStream("/home/students/jrs300/aaa.txt.enc");
	    fosd = new FileOutputStream("/home/students/jrs300/aaa.decrypt.txt");

	    // initialise AppPwdObj
	    AppPwdObj appPwd = AppPwdObj.getInstance();
	    appPwd.setValue("password");
	    
	    //get SharedPrefs
	    SharedPrefs prefs = new SharedPrefs("/home/students/jrs300/SampleCSVFile.csv");
	    
	    FileCryptor.encryptFile(fis, fos, "Newgroup", prefs);
	      
	    decryptFor = FileCryptor.decryptFile(fisd, fosd, prefs);  
	    System.out.println(decryptFor);
		
		String newString = "IT WAS the best of times, it was the worst of times, it was the age of wisdom, it was the age of foolishness, it was the epoch of belief";
	  
	    String myString = "In the beginning was the Word, and the Word was with God, and the Word was God."
	    	+ "He was with God in the beginning. Through him all things were made; without him nothing was made that has been made."
	    	+ "In him was life, and that life was the light of all mankind. "
	    	+" The light shines in the darkness, and the darkness has not overcome it.";
	    FileOutputStream fosS = new FileOutputStream("/home/students/jrs300/s.enc");
	    
	    FileCryptor.encryptString(newString, fosS, "Mygroup",prefs);
	    
	    fisd = new FileInputStream("/home/students/jrs300/s.enc");
	    fosd = new FileOutputStream("/home/students/jrs300/s.txt");
	    

	    decryptFor = FileCryptor.decryptFile(fisd, fosd, prefs);  
	    System.out.println(decryptFor);
	    
    

	
	}

}
