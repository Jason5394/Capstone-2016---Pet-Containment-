package com.example.jason.petcontainment;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by jason on 4/24/2016.
 */
public class Helper {

    public static ArrayList<String> readFile(String filename, Context context){
        File savefile = new File(context.getFilesDir().getPath().toString() + filename);
        try {
            //if (!savefile.exists())
            savefile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        ArrayList<String> stringCoords = new ArrayList<>();
        try (FileInputStream inputStream = context.openFileInput(filename);
             InputStreamReader in = new InputStreamReader(inputStream);
             BufferedReader reader = new BufferedReader(in);){
            String line;
            while ((line = reader.readLine()) != null) {
                stringCoords.add(line);
            }
            System.out.println("Saved Coordinates:");
            for (int i = 0; i < stringCoords.size(); ++i) {
                System.out.println(stringCoords.get(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringCoords;
    }
}
