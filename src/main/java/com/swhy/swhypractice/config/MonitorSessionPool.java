package com.swhy.swhypractice.config;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;

import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class MonitorSessionPool {
    /**
     * 根据一个构建任务为单位维护下方Session的Id
     **/
    private static final Map<Long, List<String>> BUILD_RUN_SESSIONS = new ConcurrentHashMap<>();

    /**
     * 以SessionId为键维护所有Session
     **/
    private static final Map<String, Session> ALL_BUILD_RUN_SESSIONS = new ConcurrentHashMap<>();

    /**
     * @param sessionId 会话Id
     * @description 连接关闭方法
     **/
    public static Long close(String sessionId) {
        List<Session> sessions = ALL_BUILD_RUN_SESSIONS.values().stream().filter(session -> session.getId().equals(sessionId)).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(sessions) || sessions.size() != 1) {
            throw new RuntimeException("获取会话信息失败");
        }
        Session session = sessions.get(0);
        if (session == null) {
            throw new RuntimeException("目标Session未找到");
        }
        Set<Map.Entry<Long, List<String>>> buildRunSessionEntrySet = BUILD_RUN_SESSIONS.entrySet();
        for (Map.Entry<Long, List<String>> entry : buildRunSessionEntrySet) {
            List<String> ids = entry.getValue();
            Iterator<String> idIterator = ids.iterator();
            while (idIterator.hasNext()) {
                String id = idIterator.next();
                if (id.equals(sessionId)) {
                    idIterator.remove();
                    ALL_BUILD_RUN_SESSIONS.remove(sessionId);
                    return entry.getKey();
                }
            }
        }
        return -1L;
    }

    /**
     * @param buildRunId 构建执行Id
     * @param log        新增的日志内容
     * @description 向监听某个构建执行实时日志的客户端推送新的日志信息
     **/
    public static void sendMessage(Long buildRunId, String log) {
        List<String> sessionIds = BUILD_RUN_SESSIONS.get(buildRunId);
        if (CollectionUtils.isEmpty(sessionIds)) {
            throw new RuntimeException("当前构建任务下方无实时日志监听客户端");
        }
        sessionIds.forEach(sessionId -> {
            Session session = ALL_BUILD_RUN_SESSIONS.get(sessionId);
            RemoteEndpoint.Async asyncRemote = session.getAsyncRemote();
            asyncRemote.sendText(log);
        });
    }

    /**
     * @param session    会话对象
     * @param buildRunId 构建执行Id
     * @description 实时监听日志客户端上线
     **/
    public static void openSession(Session session, Long buildRunId) {
        List<String> sessionIds = BUILD_RUN_SESSIONS.computeIfAbsent(buildRunId, k -> new ArrayList<>());
        sessionIds.add(session.getId());
        ALL_BUILD_RUN_SESSIONS.put(session.getId(), session);
    }

    /**
     * @param buildRunId 构建执行Id
     * @return Integer 当前构建执行日志监控客户端数量
     * @description 获取当前构建执行日志监控客户端数量
     **/
    public static Integer getAliveClientTotalNumber(Long buildRunId) {
        List<String> sessionIds = BUILD_RUN_SESSIONS.get(buildRunId);
        if (CollectionUtils.isEmpty(sessionIds)) {
            return 0;
        }
        return sessionIds.size();
    }

}
