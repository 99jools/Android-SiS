import javax.crypto.spec.IvParameterSpec;


public class Metadata {
private byte[] idLength;
private byte[] groupIdAsByteArray;
private byte[] iv;
private Metadata(String groupID, IvParameterSpec ips) {
	this.groupIdAsByteArray = groupID.getBytes("UTF-8");
	this.iv = ips.getIV();
	int len = groupIdAsByteArray.length;
	this.idLength = 
}


}
