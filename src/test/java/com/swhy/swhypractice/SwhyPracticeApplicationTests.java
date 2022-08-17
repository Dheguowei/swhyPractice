package com.swhy.swhypractice;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jcraft.jsch.Session;
import com.swhy.swhypractice.controller.LogController;
import com.swhy.swhypractice.mapper.RemotePropMapper;
import com.swhy.swhypractice.mapper.RemoteServicesMapper;
import com.swhy.swhypractice.pojo.RemoteProp;
import com.swhy.swhypractice.pojo.RemoteServices;
import com.swhy.swhypractice.service.AsyncService;
import com.swhy.swhypractice.service.impl.RemotePropServiceImpl;
import com.swhy.swhypractice.utils.SSHLinuxUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SwhyPracticeApplicationTests {
    @Autowired
    private RemotePropServiceImpl remotePropServiceImpl;
    @Autowired
    private RemotePropMapper remoteMapper;
    @Autowired
    private RemoteServicesMapper remoteServicesMapper;
    @Autowired
    private RemotePropMapper remotePropMapper;
    @Autowired
    private AsyncService asyncService;
    @Autowired
    LogController logController;
    @Test
    void remote() throws Exception {
        SSHLinuxUtils linuxUtils = new SSHLinuxUtils();
        Session session = linuxUtils.createSession("121.89.201.223", 22, "root", "WOshiWO123");
        String[] disk = linuxUtils.exeCommand(session, "df -h").get(1).trim().replaceAll("\\s+"," ").split(" ");

        String diskRate = disk[disk.length - 2];

    }
    @Test
    void test(){
        String s = "PID USER      PR  NI    VIRT    RES    SHR S %CPU %MEM     TIME+ COMMAND";
        String s1 = "7141 root      20   0  165596   3756   1900 S  0.0  0.2  22:32.07 redis-server";
        String[] keys = s.replaceAll("\\s+"," ").split(" ");
        System.out.println(keys.length);
        String[] values = s1.replaceAll("\\s+"," ").split(" ");
        System.out.println(values.length);
        JSONObject jsonObject = new JSONObject();
       for (int i = 0; i < keys.length; i++) {
           jsonObject.put(keys[i],values[i]);
       }
        System.out.println(jsonObject.toJSONString());
    }
    @Test
    void testRemoteProp(){
        List<RemoteProp> remoteProps = remotePropMapper.selectList(null);
        RemoteServices re = remoteServicesMapper.selectOne(new QueryWrapper<RemoteServices>().eq("pid", 980));
        System.out.println("指定pid查询" + re);

//        System.out.println(remoteProps);
    }
    @Test
    void testFileWatcher() throws FileNotFoundException {
        File file = new File("F:\\swhyPractice\\log\\file\\fileLog.log");
        FileInputStream fileInputStream = new FileInputStream(file);


    }
    @Test
    void testLogController(){


        JSONArray allLog = logController.getAllLogName();
        System.out.println(allLog.toJSONString());
        JSONArray log = logController.getLog("2022-07-26");
        System.out.println(log.toJSONString());
    }
}
