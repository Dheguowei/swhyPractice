package com.swhy.swhypractice.service;


import com.swhy.swhypractice.utils.FileWatcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.websocket.Session;
import java.nio.file.WatchService;
import java.util.Map;

@Component
@Slf4j
public class AsyncService {
    @Async("logFileListenerExecutor")
    public void startListenLogFileAndSendWebsocket(Session session, String filePath, String fileName, Map<Session, WatchService> map) {
        try{
            log.info("startListening {} {}",filePath,fileName);
            //FileWatcher.watcherLog(map.get(session), filePath, fileName,);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
