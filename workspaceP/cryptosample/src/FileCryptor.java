

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
         * Encrypt input stream and outputs result to a file
         * @param plaintextAsFileIn
         * @param ciphertextAsFileOut
         * @throws IOException
         * @throws GeneralSecurityException 
         */
        public static  void encryptFile(FileInputStream plaintextAsFileIn, 
        			FileOutputStream ciphertextAsFileOut, String groupID, String appPwd )
        					throws IOException, GeneralSecurityException{

            //retrieve encryption for this group from Key store 
            SecretKeySpec groupSKS = AppKeystore.getKeySpec(groupID, appPwd);

            //set up cipher for encryption
            IvParameterSpec ips = makeIV();
            Cipher encryptionCipher;
            encryptionCipher = Cipher.getInstance(CIPHER_ALGORITHM);
            encryptionCipher.init(Cipher.ENCRYPT_MODE, groupSKS, ips);

            //write metadata to FileOutputStream first
     	
           
            
            ciphertextAsFileOut.write(ips.getIV(),0,AES_BLOCKSIZE);
            CipherOutputStream cos = new CipherOutputStream(ciphertextAsFileOut, encryptionCipher);
            byte[] block = new byte[AES_BLOCKSIZE];
            int bytesRead = plaintextAsFileIn.read(block);
            while (bytesRead != -1) {
                cos.write(block, 0, bytesRead);
                bytesRead = plaintextAsFileIn.read(block);
            }
            cos.close();
            return;
            } //end EncryptFile



/**
 * Decrypts an output stream
 * @param ciphertextAsFileIn
 * @param plaintextAsFileOut
 * @throws GeneralSecurityException
 * @throws IOException
 */
        public static  void decryptFile(FileInputStream ciphertextAsFileIn, 
        				FileOutputStream  plaintextAsFileOut) throws GeneralSecurityException, IOException
                {
        	AppKeystore km = new AppKeystore("password");  // NEED TO GET FROM USER
            CipherInputStream cis;

            //initialise encryption cipher
            Cipher decryptionCipher;
            
            // read meta data from from input stream
            byte[] initVector = new byte[16];
            byte[] groupID = new byte[16];
                    try {
                        ciphertextAsFileIn.read(groupID);
                        ciphertextAsFileIn.read(initVector);
                        IvParameterSpec ips = new IvParameterSpec(initVector);

            decryptionCipher = Cipher.getInstance(CIPHER_ALGORITHM);
            decryptionCipher.init(Cipher.DECRYPT_MODE, 
            		km.getExistingKey(groupID, "password"),ips);

            cis = new CipherInputStream(ciphertextAsFileIn,decryptionCipher );

            //read and decrypt file
            byte[] block = new byte[AES_BLOCKSIZE];
            int bytesRead;

            bytesRead = cis.read(block);
            while (bytesRead != -1) {
                plaintextAsFileOut.write(block,0,bytesRead);
                bytesRead = cis.read(block);  //read next block
            }
            //close file
            plaintextAsFileOut.close();
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
                    return;
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



    
