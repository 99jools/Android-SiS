package securelyshare;

/**
 * Created by jrs300 on 04/08/14.
 */
@SuppressWarnings("serial")
public class MissingPwdException extends Exception {
   public MissingPwdException() {
        super("Keystore password not found.");
    }

}