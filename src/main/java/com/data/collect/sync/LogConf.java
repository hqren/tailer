package com.data.collect.sync;

import java.io.File;

public class LogConf {

    private String fileFormat = "localhost.log";
    private String rotateFileFormat = "localhost.DATE.log";
    private boolean rotateTypeIsHourly = true;
    private String logDir = "";
    private long position = 0;

    public LogConf(){}

    public String getLogFilePath(){
        return logDir + File.separator + fileFormat;
    }

    public String getRotateFilePath(String dateFormatValue){
        return logDir + File.separator + rotateFileFormat.replaceAll("DATE" ,dateFormatValue);
    }

    public String getFileFormat() {
        return fileFormat;
    }

    public void setFileFormat(String fileFormat) {
        this.fileFormat = fileFormat;
    }

    public String getRotateFileFormat() {
        return rotateFileFormat;
    }

    public void setRotateFileFormat(String rotateFileFormat) {
        this.rotateFileFormat = rotateFileFormat;
    }

    public String getLogDir() {
        return logDir;
    }

    public void setLogDir(String logDir) {
        this.logDir = logDir;
    }

    public boolean isRotateTypeIsHourly() {
        return rotateTypeIsHourly;
    }

    public void setRotateTypeIsHourly(boolean rotateTypeIsHourly) {
        this.rotateTypeIsHourly = rotateTypeIsHourly;
    }

    public long getPosition() {
        return position;
    }

    public void setPosition(long position) {
        this.position = position;
    }
}
