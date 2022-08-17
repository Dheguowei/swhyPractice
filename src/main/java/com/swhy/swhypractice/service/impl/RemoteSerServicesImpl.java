package com.swhy.swhypractice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.jeffreyning.mybatisplus.service.MppServiceImpl;
import com.swhy.swhypractice.mapper.RemoteServicesMapper;
import com.swhy.swhypractice.pojo.RemoteServices;
import com.swhy.swhypractice.service.RemoteSerService;
import org.springframework.stereotype.Service;

@Service("RemoteSerServicesImpl")
public class RemoteSerServicesImpl extends MppServiceImpl<RemoteServicesMapper, RemoteServices> implements RemoteSerService {
}
