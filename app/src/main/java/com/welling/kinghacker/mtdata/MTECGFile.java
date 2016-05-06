package com.welling.kinghacker.mtdata;

import com.creative.filemanage.ECGFile;

/**
 * Created by KingHacker on 5/6/2016.
 * 重写ECGFile
 */
public class MTECGFile extends ECGFile{
    public String fileName;
    public MTECGFile(ECGFile file){
        time = file.time;
    }
}
