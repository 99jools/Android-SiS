package com.example.jrs300.shareinsecrettest;

import android.content.SharedPreferences;

import java.util.Map;

/**
 * Created by jrs300 on 01/08/14.
 */
public class SharedPrefs {

    private SharedPreferences sp;

    //constructor
    public SharedPrefs(SharedPreferences sp){
        this.sp = sp;
   }

    public int getCode(String groupID){
         return  sp.getInt(groupID, 0); //0 is default value if not found
    }

    public String getID(Integer groupCode ) {
        // no reverse key so need to search set of values
        Map<String,?> sToInt =  sp.getAll();
        for(Map.Entry<String,?> entry : sToInt.entrySet()){
            if (entry.getValue() == groupCode)
                return entry.getKey();
        }
        return null;  // groupCde not found
    }

    public boolean groupExists(String groupID){

        return sp.contains(groupID);
    }

    public void addGroup(String groupID){
        //get the next available code
        int nextCode = sp.getInt("NextID",0);

        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(groupID, nextCode);
        editor.putInt("NextID", nextCode+1);

        // Commit the edits!
        editor.commit();
    } //end addGroup
}