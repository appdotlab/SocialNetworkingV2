package com.example.photouploader.Utils;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;

public class FileSearch {

    /*
     * Search a directory and return a list of all directories contained inside
     * that directory
     */

    public static ArrayList<String> getDirectoryPaths(String directory){
        ArrayList<String> pathArray = new ArrayList<>();
        File file = new File(directory);
        Log.i("Directory", "Directory : " + directory + " , " + String.valueOf(file.listFiles()) +  "Can read : " + file.exists());
        File[] listfiles = file.listFiles();
/*
        for(File listfile : listfiles){
            if(listfile.isDirectory()){
                pathArray.add(listfile.getAbsolutePath());
            }
        }
*/
        /*
        for(int i=0; i<listfiles.length; i++){
            if(listfiles[i].isDirectory()){
                pathArray.add(listfiles[i].getAbsolutePath());
            }
        }
        */
        return pathArray;
    }

    /*
     * Search a directory and return a list of all files contained inside
     * that directory
     */

    public static ArrayList<String> getFilePaths(String directory){
        ArrayList<String> pathArray = new ArrayList<>();
        File file = new File(directory);
        File[] listfiles = file.listFiles();
        /*
        for(File listfile : listfiles){
            if(listfile.isFile()){
                pathArray.add(listfile.getAbsolutePath());
            }
        }
        */
        return pathArray;
    }
}
