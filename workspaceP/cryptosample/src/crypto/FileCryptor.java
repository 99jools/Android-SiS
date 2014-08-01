package crypto;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

 
/**
 * Created by jrs300 on 09/07/14.
 */
public class FileCryptor {

	public static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
	public static final int AES_BLOCKSIZE = 16;   //16 bytes = 128 bits


	private FileCryptor() {
		// dummy constructor to prevent accidental instantiation
	}

	/**
	 * Encrypt input stream and outputs result to a file - location of output stream is set up in calling
	 * class
	 * @param fis
	 * @param fos
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	public static  void encryptFile(FileInputStream fis,
			FileOutputStream fos, String groupID, SharedPrefs prefs )
	throws IOException, GeneralSecurityException{

		Cipher encryptionCipher = initEncryptCipher(groupID, prefs);
		
		//write metadata to FileOutputStream 
		fos.write(prefs.getCode(groupID));  	 						 //writes 4 byte hashcode
		fos.write(encryptionCipher.getIV(),0,AES_BLOCKSIZE);  //IV length depends on blocksize

		//wrap fos in cipherstream to encrypt remaining blocks
		CipherOutputStream cos = new CipherOutputStream(fos, encryptionCipher);
		byte[] block = new byte[AES_BLOCKSIZE];
		int bytesRead = fis.read(block);

		//write data to output file and read next block
		while (bytesRead != -1) {
			cos.write(block, 0, bytesRead);
			bytesRead = fis.read(block);
		}
		fis.close();
		cos.close();
		return;
	} //end EncryptFile



	/**
	 * Decrypts a fileoutputstream
	 * @param fis
	 * @param plaintFos
	 * @return  groupCode - the hash of the groupID for the decrypted file
	 * @throws GeneralSecurityException
	 * @throws IOException
	 */
	public static String decryptFile(FileInputStream fis, FileOutputStream  fos, SharedPrefs prefs)
	throws GeneralSecurityException, IOException {

		// read meta data from input stream
		byte[] initVector = new byte[16];
		int groupCode = fis.read(); 
		String groupID = prefs.getID(groupCode);
		fis.read(initVector);
		IvParameterSpec ips = new IvParameterSpec(initVector);

		//setup decryption
		Cipher decryptionCipher = Cipher.getInstance(CIPHER_ALGORITHM);
		decryptionCipher.init(Cipher.DECRYPT_MODE,AppKeystore.getKeySpec(groupID,prefs),ips);
		CipherInputStream cis = new CipherInputStream(fis,decryptionCipher );

		//read and decrypt file
		byte[] block = new byte[AES_BLOCKSIZE];
		int bytesRead;
		bytesRead = cis.read(block);
		while (bytesRead != -1) {
			fos.write(block,0,bytesRead);
			bytesRead = cis.read(block);  //read next block
		}
		//close file
		fos.close();
		cis.close();

		return groupID;
	} //end decryptFile

	
	/**
	 * Encrypt a String and write to a given FileOutputStream
	 * @param plaintextAsString
	 * @param fos
	 * @param groupID
	 * @param prefs
	 * @throws GeneralSecurityException
	 * @throws IOException
	 */
	public static void encryptString(String plaintextAsString, FileOutputStream fos,
			String groupID, SharedPrefs prefs  )throws GeneralSecurityException, IOException {

		Cipher encryptionCipher = initEncryptCipher(groupID, prefs);

		//convert String to byte array using UTF-8 encoding and convert to encrypted array
		byte[] stringAsByteArray = plaintextAsString.getBytes("UTF-8");
		byte[] ciphertextAsByteArray = encryptionCipher.doFinal(stringAsByteArray);

		//write metadata to FileOutputStream 
		fos.write(prefs.getCode(groupID));  	 						 //writes 4 byte hashcode
		fos.write(encryptionCipher.getIV(),0,AES_BLOCKSIZE);  //IV length depends on blocksize

		//write out encrypted file
		fos.write(ciphertextAsByteArray);
		fos.close();

		return;
	} //end encryptString
	
	
	
	/**
	 * Sets up the necessary Cipher object
	 * @param groupID
	 * @param prefs
	 * @return
	 */
	private static Cipher initEncryptCipher(String groupID, SharedPrefs prefs){
		//retrieve encryption for this group from Key store
		SecretKeySpec groupSKS = AppKeystore.getKeySpec(groupID, prefs);

		//set up cipher for encryption
		Cipher encryptionCipher = null;;
		try {
			encryptionCipher = Cipher.getInstance(CIPHER_ALGORITHM);
			encryptionCipher.init(Cipher.ENCRYPT_MODE, groupSKS);
		} catch (GeneralSecurityException e) {
			e.printStackTrace();    // should not happen - ok to abort
		}
		return encryptionCipher;
	} // end getCipher

//********************************************************************************************                   
     
        public static IvParameterSpec dummyIV(){
             //set up dummy iv - CHANGE THIS
            byte ff = (byte) 0xff;
            byte[] dummyIV = {ff,ff,ff,ff,0x00,ff,ff,ff,ff,ff,ff,ff,ff,ff,ff,ff};
            return new IvParameterSpec(dummyIV);
       }
       
    } //end FileCryptor
 
 
 
    
