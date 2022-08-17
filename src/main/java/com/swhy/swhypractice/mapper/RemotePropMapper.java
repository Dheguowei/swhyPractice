package com.swhy.swhypractice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.swhy.swhypractice.pojo.RemoteProp;
import com.swhy.swhypractice.pojo.RemoteServices;
import org.springframework.stereotype.Repository;

@Repository("RemotePropMapper")
public interface RemotePropMapper extends BaseMapper<RemoteProp> {

}
