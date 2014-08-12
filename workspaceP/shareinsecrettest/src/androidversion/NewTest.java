package androidversion;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import org.junit.Test;

public class NewTest {

	FileInputStream fis;
	FileOutputStream fos;
	FileInputStream eis;
	FileOutputStream dos;
	AppKeystore aks;
	String myString;
	String decryptFor;

		
	@Test
	public void testEncryptFile() throws IOException, GeneralSecurityException, MissingPwdException {

		// initialise AppPwdObj
		AppPwdObj apo = AppPwdObj.makeObj();
		apo.setValue("password");
		aks = new AppKeystore();

		fis = new FileInputStream("/home/students/jrs300/dream-speech.pdf");
		fos = new FileOutputStream("/home/students/jrs300/Dropbox/ShareInSecret/test/dream-speech.pdf.xps");
//	eis = new FileInputStream("/home/students/jrs300/Dropbox/ShareInSecret/test/dream-speech.pdf.xps");
//		dos =    new FileOutputStream("/home/students/jrs300//Dropbox/ShareInSecret/test/mydecrypted file.pdf");
		myString = "In the beginning was the Word, and the Word was with God, and the Word was God."
			+ "He was with God in the beginning. Through him all things were made; without him nothing was made that has been made."
			+ "In him was life, and that life was the light of all mankind. "
			+" The light shines in the darkness, and the darkness has not overcome it.";

//			addGroup("test");
//		FileCryptor.encryptFile(fis, fos, "test");
		aks.importGroupKey("test", new File("AES key.enc"));
//		FileCryptor.decryptFile(eis, dos);
//			FileCryptor.encryptString(myString, fos, "My test group");
//			FileCryptor.decryptFile(fis, fos);
		
	}


	private void addGroup(String groupID){
		try {
			File certIn = new File("public.cert");
			File outFile = new File("AES key.enc");
			aks.addGroupKey(groupID, certIn, outFile);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		} catch (MissingPwdException e) {
			e.printStackTrace();
		}
	}

	private void importKey(String groupID){

		try {
			aks.importGroupKey(groupID, new File("AES key.enc"));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		} 
	}
}

	 