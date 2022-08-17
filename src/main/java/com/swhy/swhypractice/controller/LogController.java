package com.swhy.swhypractice.controller;

import com.alibaba.fastjson.JSONArray;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;

@Slf4j
@RestController
@CrossOrigin
public class LogController {

    @GetMapping("/getAllLogName")
    public JSONArray getAllLogName(){
        JSONArray jsonArray = new JSONArray();
        File file = new File("F:\\swhyPractice\\log\\file");
        File[] fileArray = file.listFiles();
        assert fileArray != null;
        for (File file1 : fileArray) {
            if (file1.isFile()){
                jsonArray.add(file1.getName());
                String fileName = file1.getName();

            }
        }
        return jsonArray;
    }

    @GetMapping("/getLog/{date}")
    public JSONArray getLog(@PathVariable String date){
        System.out.println(date);
        JSONArray jsonArray = new JSONArray();
        try {
            File logFile = new File("F:\\swhyPractice\\log\\file\\fileLog.log." + date + ".0");
            FileReader fileReader = new FileReader(logFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            while ((line = bufferedReader.readLine()) != null){
                jsonArray.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonArray;
    }
}
