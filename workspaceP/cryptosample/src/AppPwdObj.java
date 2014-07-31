
public class AppPwdObj {

	private static AppPwdObj instance = null;   
	private String value;
	
	protected AppPwdObj(){
		
	}
	
	public static AppPwdObj getInstance(){
		if (instance == null) 
			instance = new AppPwdObj();
		return instance;
		}
	

	public String getValue(){
		return this.value;
	}
	
	public void setValue(String value){
		this.value = value;
		return;
	}
}
