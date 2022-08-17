package com.swhy.swhypractice.pojo;

import lombok.*;
import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.util.List;
import java.util.concurrent.Future;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public final class FileLocationDescriptor {
    /**
     * 已经输出的日志文件内容（如果存在日志内容过大问题，后面会有另一种思路，我这里强行认为我的内存够用，一般情况下，某一次构建产生的日志文件不会太大）
     **/
    private List<String> log;
    /**
     * 当前已经输出的日志内容坐标
     **/
    private Long nowPosition;
    /**
     * 因为支持按行输出日志，所以保存当前输出的日志内容行数
     **/
    private Long nowLine;
    /**
     * 标识一个监控线程异步执行的结果，会用于销毁线程
     **/
    private Future<?> submit;
    /**
     * 保存当前操作的流对象，用于关闭
     **/
    private Closeable closable;
}