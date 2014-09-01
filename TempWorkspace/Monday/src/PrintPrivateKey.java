
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;

/**
 * Created by jrs300 on 01/09/14.
 */
public class PrintPrivateKey {

    public static void printPK( KeyStore mycs) {

        try {
  
        PrivateKey key = null;
        key= (PrivateKey) mycs.getKey("rsassokey", "fred".toCharArray());

        byte[] mykey = key.getEncoded();
        FileOutputStream f = new FileOutputStream(new File("/home/students/jrs300/Dropbox/SecurelyShare/Eclipse output/bks.private.key"));
        f.write(mykey);
        f.close();


        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void printSymK( byte[] mykey) {


        try {
            FileOutputStream f = new FileOutputStream(new File("/home/students/jrs300/Dropbox/SecurelyShare/Eclipse output/bks.symmetric.key"));

            f.write(mykey);
            f.close();

        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    public static void printEncrypted( FileInputStream fis){

        byte[] b = new byte[2048 / 8];
        try {
            int i = fis.read(b);
            FileOutputStream f = new FileOutputStream(new File("/home/students/jrs300/Dropbox/SecurelyShare/Eclipse output/bks.encrypted.key"));
            f.write(b);
            fis.close();
            f.close();


        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}