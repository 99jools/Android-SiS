

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * Creates a a Cipher object based on the particular encryption key retrieved from
 * the Keystore for the group in question
 *
 * This can be constructed as either an encryption or decryption cipher depending on the input parameters
 * Created by jrs300 on 07/08/14.
 */
public class MyCipher {
    public static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
    public static final String PROVIDER = "BC";
    public static final int AES_BLOCKSIZE = 16;   //16 bytes = 128 bits
    public static final int AES_KEYLENGTH = 256;  //may decide to make this a user option later

    private SecretKeySpec groupSKS;
    private String groupID;
    private Cipher mCipher;
    private byte[] iv;

    /**
     * this constructor used to create encryption cipher
     * @param groupID
     * @throws MissingPwdException
     */
    public MyCipher(String groupID) throws MissingPwdException {
    	try {
    		AppKeystore aks = new AppKeystore();
    		this.groupSKS = aks.getSKS(groupID);
    		this.groupID = groupID;
    		  Security.addProvider(new BouncyCastleProvider());
    		this.mCipher = Cipher.getInstance(CIPHER_ALGORITHM,PROVIDER);
    		mCipher.init(Cipher.ENCRYPT_MODE, groupSKS);
    		this.iv =  mCipher.getIV();  //array length will be equivalent to AES blocksize
    	} catch (GeneralSecurityException e) {
    		System.out.println(e.getMessage());
    	} 
    	}
/**
 * this constructor used to create decryption cipher
 * @param gaba
 * @param iv
 * @throws MissingPwdException
 */
    public MyCipher(byte[] gaba, byte[] iv ) throws MissingPwdException {
        try {
    		AppKeystore aks = new AppKeystore();
            this.iv = iv;
            IvParameterSpec ips =  new IvParameterSpec(iv);
            this.groupID = new String(gaba, "UTF-8");
            this.groupSKS = aks.getSKS(groupID);
            Security.addProvider(new BouncyCastleProvider());
            this.mCipher = Cipher.getInstance(CIPHER_ALGORITHM,PROVIDER);
            mCipher.init(Cipher.DECRYPT_MODE, groupSKS,ips);
        } catch (GeneralSecurityException e) {
        	System.out.println(e.getMessage());
        } catch (UnsupportedEncodingException e) {
        	System.out.println(e.getMessage());
        }
    }

    public Cipher getmCipher() {
        return mCipher;
    }

    public String getGroupID() {
        return groupID;
    }

    public byte[] getIv() {
        return iv;
    }

    public byte[] getGroupAsByteArray() {
        try {
        return groupID.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
        	System.out.println( e.getMessage());
        }
        return null;
    }


    public SecretKeySpec getGroupSKS() {
        return groupSKS;
    }

    public byte[] getGroupLength(){
        int len = this.getGroupAsByteArray().length;
        return ByteBuffer.allocate(4).putInt(len).array();
    }
}