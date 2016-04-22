package com.welling.kinghacker.mtdata;

import android.util.Log;

import com.creative.filemanage.ECGFile;
import com.welling.kinghacker.tools.PublicRes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by user on 2015/12/7.
 **/
public class ECGFilesUtils {

    public static void packECGFile(ECGFile ecgFile) throws JSONException, IOException {

        JSONObject object = new JSONObject();
        object.put("time", ecgFile.time);
        object.put("nAverageHR", ecgFile.nAverageHR);
        Log.i("tag",ecgFile.ecgData.size()+"");
        object.put("nAnalysis", ecgFile.nAnalysis);
        JSONArray array = new JSONArray();
        List<Integer> ecgData = ecgFile.ecgData;
        for (int i = 0; i < ecgData.size(); i++) {
            array.put(i, ecgData.get(i));
        }
        object.put("ecgData", array);

        File pFile = new File(PublicRes.ECGPATH);

        if (!pFile.exists()) {
            Log.i("File","created");
            pFile.mkdir();
        }
        File file = new File(pFile, System.currentTimeMillis() + ".txt");
        if (!file.exists()) {
            file.createNewFile();
        }
        FileOutputStream fos = new FileOutputStream(file);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        bos.write(object.toString().getBytes());
        bos.flush();
        fos.close();
        bos.close();
    }

    public static List<File> getECGFileList(String path) {
        List<File> list = new ArrayList<>();
        File file = new File(path);
        File[] files = file.listFiles();
        TreeMap<Long,File> tm = new TreeMap<>();

        for (File file1 : files) {
            int index = file1.getName().lastIndexOf(".");
            String text = file1.getName().substring(0, index);
            long l = Long.parseLong(text);
            tm.put(l, file1);
        }

        for (File f :tm.values()){
            list.add(f);
        }
        return list;
    }

    public static ECGFile getECGFile(File file) throws IOException, JSONException {

        FileInputStream fis = new FileInputStream(file);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buff = new byte[1024];
        int len;
        while ((len = fis.read(buff)) != -1) {
            baos.write(buff, 0, len);
        }
        baos.flush();
        String json = baos.toString();

        JSONObject object = new JSONObject(json);
        String time = object.getString("time");
        int nAverageHR = object.getInt("nAverageHR");
        int nAnalysis = object.getInt("nAnalysis");
        JSONArray array = object.getJSONArray("ecgData");
        List<Integer> ecgData = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            ecgData.add(array.getInt(i));
        }

        ECGFile ecgFile = new ECGFile();
        ecgFile.time = time;
        ecgFile.nAverageHR = nAverageHR;
        ecgFile.nAnalysis = nAnalysis;
        ecgFile.ecgData = ecgData;

        return ecgFile;
    }
   static public ECGFile getLastECGFile(){
        List<File> files = getECGFileList(PublicRes.ECGPATH);
        if (files != null && files.size() > 0){
            try {
                return getECGFile(files.get(0));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
