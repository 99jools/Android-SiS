package crypto;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

public class SharedPrefs {
	private Map<String, Integer > sToInt;
	private Map<Integer, String > iToString;
	private String prefsLoc;

	//constructor
	public SharedPrefs(String filename) throws IOException {

		this.sToInt = new HashMap<String,Integer>();
		this.iToString = new HashMap<Integer,String>();
		this.prefsLoc = filename;
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
	
	public boolean codeExists(String GroupID){
		return sToInt.containsKey(GroupID);
	}

	public void addGroup(String groupID){
		int nextID = sToInt.get("NextID");
		this.sToInt.put(groupID,nextID);
		this.iToString.put(nextID,groupID);

		this.sToInt.put("NextID", nextID+1);
		this.iToString.put(nextID+1,"NextID");
		PrintStream fs = null;
		try {
			fs = new PrintStream(new File(prefsLoc));
			//write updated map to disk
			for (Map.Entry<String, Integer> entry : sToInt.entrySet()) {
					String out = entry.getKey() + "," + entry.getValue();
					fs.println(out);
				}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			fs.close();
		}
		
		return;
	} //end addGroup
}
