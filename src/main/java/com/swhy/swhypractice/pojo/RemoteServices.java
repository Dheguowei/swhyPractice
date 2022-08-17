package com.swhy.swhypractice.pojo;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "remote_services")
public class RemoteServices {
    @MppMultiId
    @TableField("address")
    private String address;
    @MppMultiId
    @TableField("pid")
    private int pid;
}
