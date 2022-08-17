package com.swhy.swhypractice.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "remote_prop")
public class RemoteProp {
    @TableId
    private String address;
    private int port;
    private String user;
    private String pwd;
}
