package com.swhy.swhypractice.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jcraft.jsch.Session;
import com.swhy.swhypractice.mapper.RemoteServicesMapper;
import com.swhy.swhypractice.pojo.RemoteProp;
import com.swhy.swhypractice.pojo.RemoteServices;
import com.swhy.swhypractice.service.impl.RemotePropServiceImpl;
import com.swhy.swhypractice.utils.SSHLinuxUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RestController
@CrossOrigin
public class ServicesController {

    @Resource(name = "RemoteServicesMapper")
    private RemoteServicesMapper remoteServicesMapper;
    @Resource(name = "RemotePropServiceImpl")
    private RemotePropServiceImpl remotePropServiceImpl;

    /**
     *
     * @return JSONArray 是目前的服务所占用的内存，以及Cpu使用量
     * @throws Exception
     */
    @GetMapping("/getAllServices")
    public JSONArray getServices() throws Exception {
        SSHLinuxUtils linuxUtils = new SSHLinuxUtils();
        List<RemoteServices> remoteServices = remoteServicesMapper.selectList(null);
        JSONArray jsonArray = new JSONArray();
        for (RemoteServices service : remoteServices) {
            String address = service.getAddress();
            //System.out.println(address);
            int pid = service.getPid();
            //System.out.println(pid);
            RemoteProp remoteProp = remotePropServiceImpl.getById(address);
            Session session = linuxUtils.createSession(remoteProp.getAddress(), remoteProp.getPort(), remoteProp.getUser(), remoteProp.getPwd());
            List<String> resultList = linuxUtils.exeCommand(session, "top -b -n 1 -p" + " " + pid);
            String keys = resultList.get(resultList.size() - 2);
            //keys.split();
            String values = resultList.get(resultList.size() - 1);
            String[] key = keys.replaceAll("\\s+"," ").replaceAll("%","").split(" ");
            String[] value = values.replaceAll("\\s+"," ").split(" ");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("ADDRESS",address);
            for (int i = 0; i < key.length; i++) {
                jsonObject.put(key[i],value[i]);
            }
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }
    @GetMapping("/getService/{serviceName}")
    public JSONArray getService(@PathVariable String serviceName){
        JSONArray jsonArray = new JSONArray();

        return jsonArray;
    }
}
