package com.example.photouploader.Utils;

import android.os.Environment;

public class FilePath {

    public static String ROOT_DIR = Environment.getExternalStorageDirectory().getPath();
    public static String PICTURES = ROOT_DIR + "/Pictures";
    public String CAMERA = ROOT_DIR + "/DCIM/camera";

}
