package com.data.collect.sync;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Tailer
 */
public class Tailer {

    private static boolean productEnv = true;

    private static long SleepTime1 = 500;
    private static long SleepTime2 = 1000*10;
    private static boolean keepRunning = true;
    private static Queue<String> globalMessageQueue = new ConcurrentLinkedQueue<String>();

    /**
     * three args: logDir filePosition env
     * eg 1:/opt/server/logs 0 test
     * eg 2:/opt/server/logs 0
     * eg 3:/opt/server/logs 1678
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        //hook
        final Thread mainThread = Thread.currentThread();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                keepRunning = false; //
                System.out.println(DateUtil.getNowTime()+":do shutdown hook");
                try {
                    mainThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        //config
        LogConf logConf = new LogConf();
        logConf.setFileFormat("localhost.log");
        logConf.setRotateFileFormat("localhost.DATE.log"); // DATE IS FIXED
        logConf.setRotateTypeIsHourly(false);

        //args: logDir and FilePosition
        if (args.length >= 1) {
            logConf.setLogDir(args[0]);
        }
        long checkPosition = 0;
        if (args.length >= 2) {
            logConf.setPosition(Long.valueOf(args[1]));
        }

        if(args.length >= 3){
            productEnv = false;
        }

        //execute
        monitorFile(logConf);
    }

    /**
     * Monitor File and load to database.
     * @param conf
     * @throws IOException
     */
    private static void monitorFile(LogConf conf) throws IOException {

        //get rotate infos
        Date nextTime = DateUtil.getTomorrowDate();
        String rotateTimeValue = DateUtil.getTodayDate();
        if(conf.isRotateTypeIsHourly()){
            nextTime = DateUtil.getNextHour();
            rotateTimeValue = DateUtil.getThisHour();
        }

        //file init
        File file = new File(conf.getLogFilePath());
        if (!file.exists()) {
            System.err.println(DateUtil.getNowTime() + ":" + conf.getLogFilePath() + " is not exist.");
            return;
        }

        long checkPosition = conf.getPosition();
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
        if (checkPosition > 0L) randomAccessFile.seek(checkPosition);

        System.out.println("==========server start==========");
        System.out.println(DateUtil.getNowTime()+":nextTime:" + DateUtil.formatDateTime(nextTime));
        System.out.println(DateUtil.getNowTime()+":rotateTimeValue:"+rotateTimeValue);
        System.out.println(DateUtil.getNowTime()+":file:" + conf.getLogFilePath());
        System.out.println(DateUtil.getNowTime()+":rotateTypeIsHourly:" + conf.isRotateTypeIsHourly());

        //monitor file
        String currentLine = null;
        long lastoffset = 0;

        while (keepRunning) {
            if ((currentLine = randomAccessFile.readLine()) != null) {
                System.out.println(currentLine);

                lastoffset = randomAccessFile.getChannel().position();
                globalMessageQueue.add(currentLine);
                saveLog(10);

                //sleep
                try { Thread.sleep(SleepTime1); } catch (InterruptedException e) { Thread.currentThread().interrupt();break; }
            } else {

                //sleep
                try { Thread.sleep(SleepTime2); } catch (InterruptedException e) { Thread.currentThread().interrupt();break; }

                //判断当前时间，是否跨天，如果跨天则重新切换
                Date dt = new Date();
                if (nextTime.before(dt)) {

                    saveLog(0); //历史日志全部提交

                    while (true) {
                        //check rotate File exist
                        String rotateFilePath = conf.getRotateFilePath(rotateTimeValue);
                        if (new File(rotateFilePath).exists()) {
                            System.out.println(DateUtil.getNowTime() + ":found new file:"+rotateFilePath);
                            file = new File(conf.getLogFilePath());

                            // resign
                            if(conf.isRotateTypeIsHourly()){
                                nextTime = DateUtil.getNextHour();
                                rotateTimeValue = DateUtil.getThisHour();
                            } else {
                                nextTime = DateUtil.getTomorrowDate();
                                rotateTimeValue = DateUtil.getTodayDate();
                            }

                            System.out.println(DateUtil.getNowTime()+":nextTime:"+ DateUtil.formatDateTime(nextTime));
                            System.out.println(DateUtil.getNowTime()+":rotateTimeValue:"+rotateTimeValue);
                            System.out.println(DateUtil.getNowTime()+":file:" + conf.getLogFilePath());
                            System.out.println(DateUtil.getNowTime()+":rotateTypeIsHourly:" + conf.isRotateTypeIsHourly());

                            break;
                        } else {
                            try { Thread.sleep(SleepTime2); } catch (InterruptedException e) { Thread.currentThread().interrupt();break; }
                        }
                    }
                    randomAccessFile.close();
                    randomAccessFile = new RandomAccessFile(file, "r");
                } else {
                    System.out.println(DateUtil.getNowTime() + ":no new record.");
                }
            }
        }

        //Closing Processes
        randomAccessFile.close();
        saveLog(0);

        //Save filepath and file position
        System.out.println("==========server stop==========");
        System.out.println(DateUtil.getNowTime() + ":filePath，offset=" + conf.getLogFilePath() + " " + lastoffset);
        System.out.println("==========server stop==========");
    }


    /**
     * SaveLog to mysql
     * @param maxSize
     * @return
     */
    private static int saveLog(int maxSize) {

        int size = globalMessageQueue.size();
        int cnt = 0;

        if (size >= maxSize || maxSize == 0) {
            List<String> list = new ArrayList<String>();
            //提取queue数据到list
            for (int i = 0; i < size; i++) {
                String message = globalMessageQueue.poll();
                if (message == null) {
                    break;
                }
                list.add(message);
            }

            //TODO something to do with Database
            //TODO
            for(String message:list){
                System.out.println(message);
            }
            //TODO something to do with Database
        }

        return cnt;
    }
}
