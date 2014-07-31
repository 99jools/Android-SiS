import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SharedPrefs {
	private Map<String, Integer > sToInt;
	private Map<Integer, String > iToString;

	//constructor
	public SharedPrefs(String filename) throws IOException {

		this.sToInt = new HashMap<String,Integer>();
		this.iToString = new HashMap<Integer,String>();
		//Input file which needs to be parsed
		BufferedReader fileReader = null;

		//Set the delimiter used in file
		final String DELIMITER = ",";

		try {
			String line = "";
			//Create the file reader
			fileReader = new BufferedReader(new FileReader(filename));

			//Read the file line by line
			while ((line = fileReader.readLine()) != null)
			{
				//Get all tokens available in line
				String[] tokens = line.split(DELIMITER);
				String s = tokens[0];
				int i = Integer.parseInt(tokens[1]);
				sToInt.put(s, i);
				iToString.put(i,s);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally
		{
			try {
				fileReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	 public int getCode(String groupID){
		 return this.sToInt.get(groupID);  //returns the corresponding int value
	 }
	 
	 public String getID(Integer code){
		 return iToString.get(code);
	 }

}
