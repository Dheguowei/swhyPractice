package com.swhy.swhypractice.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jcraft.jsch.Session;
import com.swhy.swhypractice.config.WebSocketClientConfig;
import com.swhy.swhypractice.mapper.RemotePropMapper;
import com.swhy.swhypractice.mapper.RemoteServicesMapper;
import com.swhy.swhypractice.pojo.RemoteProp;
import com.swhy.swhypractice.pojo.RemoteServices;
import com.swhy.swhypractice.service.impl.RemotePropServiceImpl;
import com.swhy.swhypractice.utils.SSHLinuxUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
@EnableScheduling
public class ScheduleServices {
    @Autowired
    private WebSocketClientConfig webSocketClientConfig;
    @Autowired
    private RemoteServicesMapper remoteServicesMapper;
    @Autowired
    private RemotePropServiceImpl remotePropServiceImpl;
    @Autowired
    private RemotePropMapper remotePropMapper;
    private long lastTimeFileSize = 0;
    @Scheduled(cron = "0/3 * * * * ?")//不支持毫秒级

    public void pushProgressMessage() throws Exception {
        List<RemoteServices> remoteServices = remoteServicesMapper.selectList(null);
        //log.info("已保存的服务配置信息" + remoteServices.toString());
        SSHLinuxUtils sshLinuxUtils = new SSHLinuxUtils();
        JSONArray jsonArray = new JSONArray();
        for (RemoteServices service : remoteServices) {
            String address = service.getAddress();
            //System.out.println(address);
            int pid = service.getPid();
            //System.out.println(pid);
            RemoteProp remoteProp = remotePropServiceImpl.getById(address);

            Session session = sshLinuxUtils.createSession(remoteProp.getAddress(), remoteProp.getPort(), remoteProp.getUser(), remoteProp.getPwd());

            //获取总内存使用率，

            List<String> memList = sshLinuxUtils.exeCommand(session, "free");
            String[] memResList = memList.get(1).trim().replaceAll("\\s+", " ").split(" ");
            float memPercent = (1 - (Float.parseFloat(memResList[memResList.length - 1]) / Float.parseFloat(memResList[1]))) * 100;

            //获取总CPU使用率
            List<String> cpuList = sshLinuxUtils.exeCommand(session, "top -b -n 1");
            String[] cpuResList = cpuList.get(2).trim().split(",");
            String  cpuPercentString = cpuResList[3].trim().replaceAll("id","").trim();
            float cpuPercent = (100 - Float.parseFloat(cpuPercentString));
            //获取每个进程的详细信息
            List<String> resultList = sshLinuxUtils.exeCommand(session, "top -b -n 1 -p" + " " + pid);
            String keys = resultList.get(resultList.size() - 2).trim();
            //keys.split();
            String values = resultList.get(resultList.size() - 1).trim();

            String[] key = keys.replaceAll("\\s+"," ").replaceAll("%","").split(" ");
            //System.out.println(Arrays.toString(key));
            String[] value = values.replaceAll("\\s+"," ").split(" ");
            //System.out.println(Arrays.toString(value));
            session.disconnect();
            Date date = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            String formatTime = simpleDateFormat.format(date);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("ADDRESS",address);
            jsonObject.put("PORT",String.valueOf(remoteProp.getPort()));
            jsonObject.put("PWD",remoteProp.getPwd());
            jsonObject.put("TIME",formatTime);
            jsonObject.put("TOTAL_MEM",String.valueOf(memPercent));
            jsonObject.put("TOTAL_CPU",String.valueOf(cpuPercent));
            for (int i = 0; i < key.length; i++) {
                jsonObject.put(key[i],value[i]);
            }
            jsonArray.add(jsonObject);
        }
        webSocketClientConfig.sendOneJsonObject("22",jsonArray);
    }

    @Scheduled(cron = "0/5 * * * * ?")
    public void pushHostMessage() throws Exception {
        List<RemoteProp> remoteProps = remotePropMapper.selectList(null);
        SSHLinuxUtils sshLinuxUtils = new SSHLinuxUtils();
        JSONArray jsonArray = new JSONArray();
        for (RemoteProp remoteProp : remoteProps) {
            Session session = sshLinuxUtils.createSession(remoteProp.getAddress(), remoteProp.getPort(), remoteProp.getUser(), remoteProp.getPwd());
            JSONObject jsonObject = new JSONObject();
            //获取总内存使用率，
            List<String> memList = sshLinuxUtils.exeCommand(session, "free");
            String[] memResList = memList.get(1).trim().replaceAll("\\s+", " ").split(" ");
            float memPercent = (1 - (Float.parseFloat(memResList[memResList.length - 1]) / Float.parseFloat(memResList[1]))) * 100;

            //获取总CPU使用率
            List<String> cpuList = sshLinuxUtils.exeCommand(session, "top -b -n 1");
            String[] cpuResList = cpuList.get(2).trim().split(",");
            String  cpuPercentString = cpuResList[3].trim().replaceAll("id","").trim();
            float cpuPercent = (100 - Float.parseFloat(cpuPercentString));

            //获取cpu个数
            int cpuNum = sshLinuxUtils.exeCommand(session, "cat /proc/cpuinfo | grep processor").size();

            //获取总内存
            String[] split = sshLinuxUtils.exeCommand(session, "free -h").get(1).trim().replaceAll("\\s+", " ").split(" ");
            String totalMem = split[1];

            //获取主机下行上行速率
            List<String> sarList = sshLinuxUtils.exeCommand(session, "sar -n DEV 1 1");
            String[] netRate = sarList.get(sarList.size() - 2).replaceAll("\\s+", " ").trim().split(" ");
            String upLoadRate = netRate[4];
            String downLoadRate = netRate[5];

            //获取负载信息 和 连接数量
            String[] uptimes = sshLinuxUtils.exeCommand(session, "uptime").get(0).trim().split(",");
            //System.out.println(uptimes.length);
            String connNum = uptimes[2].trim().replaceAll("users", "").trim();
            String loadRate = uptimes[uptimes.length - 2].trim();


            //获取硬盘使用率
            String[] disk = sshLinuxUtils.exeCommand(session, "df -h").get(1).trim().replaceAll("\\s+"," ").split(" ");
            String diskPercent = disk[disk.length - 2].replace("%","");
            session.disconnect();
            jsonObject.put("TotalCpuPercent",String.valueOf(cpuPercent));
            jsonObject.put("TotalMemPercent",String.valueOf(memPercent));
            jsonObject.put("DiskPercent",diskPercent);
            jsonObject.put("CpuNum",String.valueOf(cpuNum));
            jsonObject.put("TotalMem",totalMem);
            jsonObject.put("UpLoadRate",upLoadRate);
            jsonObject.put("DownLoadRate",downLoadRate);
            jsonObject.put("LoadRate",loadRate);
            jsonObject.put("ConnNum",connNum);
            jsonObject.put("Address",remoteProp.getAddress());
            jsonArray.add(jsonObject);
        }
        webSocketClientConfig.sendOneJsonObject("33",jsonArray);
    }

    @Scheduled(cron = "0/3 * * * * ?")
    public void pushLog(){
        final JSONArray jsonArray = new JSONArray();
        final Thread readLogThread = new Thread(new Runnable() {
            final File logFile = new File("F:\\swhyPractice\\log\\file\\fileLog.log");
            @Override
            public void run() {
                String line;
                try {
                    if(logFile.length() > lastTimeFileSize){
                        RandomAccessFile randomAccessFile = new RandomAccessFile(logFile,"r");
                        randomAccessFile.seek(lastTimeFileSize);
                        while (randomAccessFile.readLine() != null){
                            line = new String(randomAccessFile.readLine().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                            jsonArray.add(line);
                        }
                        webSocketClientConfig.sendOneJsonObject("44",jsonArray);
                        lastTimeFileSize = randomAccessFile.length();
                        randomAccessFile.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                try {
//                    while ((line = bufferedReader.readLine()) != null) {
//                        // 将实时日志通过WebSocket发送给客户端，给每一行添加一个HTML换
//                        jsonArray.add(line);
//                        webSocketController.sendOneJsonObject("44",jsonArray);
//                        System.out.println(line);
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            }
        });
        readLogThread.start();



    }



}
