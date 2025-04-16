package com.yupi.springbootinit.service.impl.inner;

import com.yupi.apicommon.service.InnerUserInterfaceInfoService;
import com.yupi.springbootinit.service.UserInterfaceInfoService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@DubboService
public class InnerUserInterfaceInfoServiceImpl implements InnerUserInterfaceInfoService {

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    /**
     * 接口调用次数统计
     * @param interfaceId
     * @param userId
     * @return
     */
    @Override
    public boolean invokeCount(long interfaceId, long userId) {
        // 调用注入的userInterfaceInfoService 的 invokeCount方法
        return userInterfaceInfoService.invokeCount(interfaceId, userId);
    }
}
