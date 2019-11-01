package com.example.photouploader;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.photouploader.Utils.FilePath;
import com.example.photouploader.Utils.FileSearch;

import java.util.ArrayList;

public class GalleryFragment extends Fragment {

    // widgets
    private GridView gridView;
    private ProgressBar progressBar;
    private Spinner spinner;
    private ImageView galleryImage;

    // vars
    private ArrayList<String> directories;
    private static final int NUM_GRID_COLUMNS = 3;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);

        galleryImage = (ImageView) view.findViewById(R.id.galleryImageView);
        gridView = (GridView) view.findViewById(R.id.gridView);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        directories = new ArrayList<>();

        init();
        return view;
    }

    private void init(){
        FilePath filePaths = new FilePath();
        if(FileSearch.getDirectoryPaths(FilePath.PICTURES) != null){
            directories = FileSearch.getDirectoryPaths(filePaths.PICTURES);
        }
        //Log.i("Directory", String.valueOf(directories));
        directories.add(filePaths.CAMERA);
        //Log.i("Directory", String.valueOf(directories));
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, directories);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setupGridView(directories.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setupGridView(String selectedDirectory){
        final ArrayList<String> imgURLs = FileSearch.getFilePaths(selectedDirectory);
        int gridWidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = gridWidth/NUM_GRID_COLUMNS;
        gridView.setColumnWidth(imageWidth);
    }
}
