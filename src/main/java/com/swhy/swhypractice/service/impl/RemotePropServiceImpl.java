package com.swhy.swhypractice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.swhy.swhypractice.mapper.RemotePropMapper;
import com.swhy.swhypractice.pojo.RemoteProp;
import com.swhy.swhypractice.service.RemotePropService;
import org.springframework.stereotype.Service;

@Service("RemotePropServiceImpl")
public class RemotePropServiceImpl extends ServiceImpl<RemotePropMapper,RemoteProp> implements RemotePropService {
}
