package com.swhy.swhypractice.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jcraft.jsch.Session;
import com.swhy.swhypractice.mapper.RemotePropMapper;
import com.swhy.swhypractice.mapper.RemoteServicesMapper;
import com.swhy.swhypractice.pojo.RemoteProp;
import com.swhy.swhypractice.pojo.RemoteServices;
import com.swhy.swhypractice.service.impl.RemotePropServiceImpl;
import com.swhy.swhypractice.service.impl.RemoteSerServicesImpl;
import com.swhy.swhypractice.utils.SSHLinuxUtils;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
@Slf4j
@RestController
@CrossOrigin
public class PropController {
    @Autowired
    private RemotePropServiceImpl remotePropServiceImpl;
    @Autowired
    private RemotePropMapper remotePropMapper;
    @Autowired
    private RemoteServicesMapper remoteServicesMapper;
    @Autowired
    private RemoteSerServicesImpl remoteSerServiceImpl;
    String hostIP;
    int hostPort;
    String hostName;
    String hostPwd;
    /**
    @ApiOperation(value = "接受新保存的服务器配置，并输出服务器目前所有的进程")
    @GetMapping("/getRemoteProgress")
    public JSONArray get() throws Exception {
        List<String[]> res = new ArrayList<>();
        List<String[]> re = new ArrayList<>();
        JSONArray ja = new JSONArray();
        SSHLinuxUtils ssh = new SSHLinuxUtils();
        List<RemoteProp> remoteProps = remotePropMapper.selectList(null);
        for (RemoteProp remoteProp : remoteProps) {
            String address = remoteProp.getAddress();
            int port = remoteProp.getPort();
            String user = remoteProp.getUser();
            String pwd = remoteProp.getPwd();
            Session session = ssh.createSession(address, port, user, pwd);
            List<String> list = ssh.exeCommand(session, "ps -ef");
            for (String s : list) {
                res.add(s.replaceAll("\\s+", " ").split(" "));
            }
            for (int i = 1; i < res.size(); i++) {
                String[] strings = res.get(i);
                String[] newStrings = new String[8];
                int len = strings.length;
                StringBuilder stringMore = new StringBuilder();
                for (int i1 = 7; i1 < len; i1++) {
                    stringMore.append(strings[i1]).append(" ");
                }
                for (int j = 0; j < newStrings.length; j++) {
                    if (j != 7){
                        newStrings[j] = strings[j];
                    }else {
                        newStrings[j] = stringMore.toString();
                    }
                }
                re.add(newStrings);
            }
            String[] keys = res.get(0);

            for (String[] strings : re) {
                JSONObject jo = new JSONObject();
                for (int i = 0; i < keys.length; i++) {
                    jo.put(keys[i],strings[i]);
                }
                ja.add(jo);
            }
        }
        return ja;
    }*/

    @ApiOperation(value = "保存远程服务器配置，并存入Mysql",notes = "使用Mybatis-Plus解决了重复插入问题")
    @PostMapping("/saveProp")
    public String saveProp(@RequestBody JSONObject userMessage){
        hostIP = userMessage.getString("address");
        hostPort = Integer.parseInt(userMessage.getString("port"));
        hostName = userMessage.getString("user");
        hostPwd = userMessage.getString("pwd");
        boolean flag = false;
        String services = userMessage.getString("services");
        RemoteProp remoteProp = new RemoteProp(hostIP, hostPort, hostName, hostPwd);
        SSHLinuxUtils sshLinuxUtils = new SSHLinuxUtils();
        Session session = null;
        try {
            session = sshLinuxUtils.createSession(hostIP, hostPort, hostName, hostPwd);
            List<String> list = sshLinuxUtils.exeCommand(session, "ps -ef | grep -w " + services + " | grep -v grep");
            String s = list.get(0);
            log.info("查询进程返回结果：" + s);
            String[] strings = s.replaceAll("\\s+", " ").split(" ");
            RemoteServices remoteServices = new RemoteServices();
            remoteServices.setAddress(hostIP);
            remoteServices.setPid(Integer.parseInt(strings[1]));
            boolean saveServiceResult = remoteSerServiceImpl.saveOrUpdateByMultiId(remoteServices);
            log.info("remote_service保存结果:" + saveServiceResult);
            boolean savePropResult = remotePropServiceImpl.saveOrUpdate(remoteProp);
            log.info("remote_prop保存结果:" + savePropResult);
            flag = saveServiceResult && savePropResult;
        } catch (Exception e) {
            return "false";
        }
        return flag ? "success" : "false";

    }

    @GetMapping("/showProp")
    public JSONArray showProp() throws Exception {
        JSONArray jsonArray = new JSONArray();
        List<RemoteProp> remoteProps = remotePropMapper.selectList(null);
        List<RemoteServices> remoteServices = remoteServicesMapper.selectList(null);
        SSHLinuxUtils sshLinuxUtils = new SSHLinuxUtils();
        for (RemoteProp remoteProp : remoteProps) {
            JSONObject jsonObject = (JSONObject)JSONObject.toJSON(remoteProp);
            ArrayList<String> stringList = new ArrayList<>();
            Session session = sshLinuxUtils.createSession(remoteProp.getAddress(),remoteProp.getPort(),remoteProp.getUser(),remoteProp.getPwd());
            for (RemoteServices remoteService : remoteServices) {
                if (Objects.equals(remoteProp.getAddress(), remoteService.getAddress())){
                    int pid = remoteService.getPid();
                    List<String> list = sshLinuxUtils.exeCommand(session, "ps -p " + pid + " o comm=");
                    stringList.add(list.get(0));
                }
            }
            jsonObject.put("services",stringList);
            jsonArray.add(jsonObject);
        }

        return jsonArray;
    }

    @PostMapping("/deleteProp")
    public boolean deleteProp(@RequestBody JSONObject userMessage){
        System.out.println(userMessage);
        hostIP = userMessage.getString("address");
        hostPort = Integer.parseInt(userMessage.getString("port"));
        hostName = userMessage.getString("user");
        hostPwd = userMessage.getString("pwd");
        String pid = userMessage.getString("pid");
        RemoteServices remoteServices = new RemoteServices();
        remoteServices.setAddress(hostIP);
        remoteServices.setPid(Integer.parseInt(pid));
        log.info("被删除的配置：" + remoteServices);
        return remoteSerServiceImpl.deleteByMultiId(remoteServices);

    }


}
