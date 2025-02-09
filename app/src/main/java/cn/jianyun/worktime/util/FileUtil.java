package cn.jianyun.worktime.util;


import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class FileUtil {

    public static boolean saveData(Context context, String fileName, String value) {
        try {
            context.getFilesDir();
            OutputStream out = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            Writer writer = new OutputStreamWriter(out);
            try {
                writer.write(value);
                return true;
            } finally {
                writer.close();
            }
        } catch (Exception e) {

        }
        return false;
    }

    public static String loadData(Context context, String fileName, boolean withBreak) {
        BufferedReader reader = null;
        StringBuilder data = new StringBuilder();
        try {
            InputStream in = context.openFileInput(fileName);
            reader = new BufferedReader(new InputStreamReader(in));
            String line = new String();
            while ((line = reader.readLine()) != null) {
                data.append(line);
                if(withBreak){
                    data.append("\n");
                }
            }
            return data.toString();
        } catch (Exception e) {

        } finally {
            try {
                reader.close();
            } catch (Exception e) {

            }
        }
        return "";
    }

    public static boolean remove(Context context, String fileName){
        File[] files = context.getFilesDir().listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().equals(fileName);
            }
        });
        if(files.length > 0){
            return files[0].delete();
        }
        return true;
    }
}
