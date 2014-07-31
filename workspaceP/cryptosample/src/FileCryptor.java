

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
        			FileOutputStream fos, String groupID )
        					throws IOException, GeneralSecurityException{
    
            //retrieve encryption for this group from Key store 
            SecretKeySpec groupSKS = AppKeystore.getKeySpec(groupID, appPwd);

            //set up cipher for encryption
            IvParameterSpec ips = makeIV();
            Cipher encryptionCipher;
            encryptionCipher = Cipher.getInstance(CIPHER_ALGORITHM);
            encryptionCipher.init(Cipher.ENCRYPT_MODE, groupSKS, ips);

            //write metadata to FileOutputStream first
            fos.write(groupID.hashCode());   //writes 4 byte hashcode
            fos.write(ips.getIV(),0,AES_BLOCKSIZE);  //IV length depends on blocksize
            
            //wrap cipherFos in cipherstream to encrypt remaining blocks
            CipherOutputStream cos = new CipherOutputStream(fos, encryptionCipher);
            byte[] block = new byte[AES_BLOCKSIZE];
            int bytesRead = fis.read(block);
                  
            //write data to output file and read next block
            while (bytesRead != -1) {
                cos.write(block, 0, bytesRead);
                bytesRead = fis.read(block);
            }
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
        public static  int decryptFile(FileInputStream fis, FileOutputStream  fos)
        throws GeneralSecurityException, IOException {
    
        	CipherInputStream cis;
        	Cipher decryptionCipher;

        	// read meta data from from input stream
        	int groupCode;
        	byte[] initVector = new byte[16];

        	try {
        		//get meta data
        		groupCode = fis.read();  
        		String groupID = 
        		fis.read(initVector);
        		IvParameterSpec ips = new IvParameterSpec(initVector);

        		decryptionCipher = Cipher.getInstance(CIPHER_ALGORITHM);
        		decryptionCipher.init(Cipher.DECRYPT_MODE, 
            		AppKeystore.getKeySpec(groupID),ips);

            cis = new CipherInputStream(fis,decryptionCipher );

            //read and decrypt file
            byte[] block = new byte[AES_BLOCKSIZE];
            int bytesRead;

            bytesRead = cis.read(block);
            while (bytesRead != -1) {
                plaintFos.write(block,0,bytesRead);
                bytesRead = cis.read(block);  //read next block
            }
            //close file
            plaintFos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InvalidKeyException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (NoSuchPaddingException e) {
                        e.printStackTrace();
                    } catch (InvalidAlgorithmParameterException e) {
                        e.printStackTrace();
                    } catch (UnrecoverableKeyException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                    return 1;
        } //end decryptFile

/**
 * Encrypt a String and write to a given FileOutputStream
 * @param plaintextAsString
 * @param ciphertextAsFileOut
 * @param aliasArray - the GroupID which is used to retrieve
 * 													 the key from the keystore
 * @throws GeneralSecurityException
 * @throws IOException
 */
        public static void encryptString(String plaintextAsString, FileOutputStream ciphertextAsFileOut, GroupCredentials gc )
        					throws GeneralSecurityException, IOException {
        	

            //set up dummy iv - CHANGE THIS
            IvParameterSpec ips = makeIV();

            Cipher encryptionCipher;
            encryptionCipher = Cipher.getInstance(CIPHER_ALGORITHM);
            encryptionCipher.init(Cipher.ENCRYPT_MODE, km.getExistingKey(gc), ips);

            //convert String to byte array using UTF-8 encoding and convert to encrypted array
            byte[] stringAsByteArray = plaintextAsString.getBytes("UTF-8");
            byte[] ciphertextAsByteArray = encryptionCipher.doFinal(stringAsByteArray);

            //write out metadata to output file first
            ciphertextAsFileOut.write(ips.getIV());

            //write out encrypted file
            ciphertextAsFileOut.write(ciphertextAsByteArray);
            ciphertextAsFileOut.close();

            return;
        } //end encryptString

        public static void makeNewGroup(byte[] groupID){
        	
        }

        public static IvParameterSpec makeIV(){

        	//set up dummy iv - CHANGE THIS
            byte ff = (byte) 0xff;
            byte[] dummyIV = {ff,ff,ff,ff,0x00,ff,ff,ff,ff,ff,ff,ff,ff,ff,ff,ff};
            return new IvParameterSpec(dummyIV);
       }

    } //end FileCryptor



    
