package com.yupi.springbootinit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.apicommon.model.entity.UserInterfaceInfo;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.model.dto.userinterfaceinfo.UserInterfaceInfoQueryRequest;
import com.yupi.springbootinit.mapper.UserInterfaceInfoMapper;
import com.yupi.springbootinit.service.UserInterfaceInfoService;
import org.springframework.stereotype.Service;

/**
* @author 张泽鹏
* @description 针对表【user_interface_info(用户调用接口关系表)】的数据库操作Service实现
* @createDate 2024-04-04 20:18:01
*/
@Service
public class UserInterfaceInfoServiceImpl extends ServiceImpl<UserInterfaceInfoMapper, UserInterfaceInfo>
    implements UserInterfaceInfoService{

    @Override
    public void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add) {
        if (userInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long userId = userInterfaceInfo.getUserId();
        Long interfaceInfoId = userInterfaceInfo.getInterfaceId();
        Integer leftNum = userInterfaceInfo.getLeftNum();

        // 创建时，参数不能为空
        if (add) {
            if(userId <= 0){
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
            }
            if(interfaceInfoId <= 0){
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口不存在");
            }
        }
        // 有参数则校验
        if (leftNum < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "剩余调用次数不能小于0");
        }
    }

    @Override
    public QueryWrapper<UserInterfaceInfo> getQueryWrapper(UserInterfaceInfoQueryRequest userInterfaceInfoQueryRequest) {
        if (userInterfaceInfoQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = userInterfaceInfoQueryRequest.getId();
        Long userId = userInterfaceInfoQueryRequest.getUserId();
        Long interfaceInfoId = userInterfaceInfoQueryRequest.getInterfaceId();
        Integer totalNum = userInterfaceInfoQueryRequest.getTotalNum();
        Integer leftNum = userInterfaceInfoQueryRequest.getLeftNum();
        Integer status = userInterfaceInfoQueryRequest.getStatus();

        QueryWrapper<UserInterfaceInfo> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq(id != null, "id", id);
        queryWrapper.eq(userId != null, "userId", userId);
        queryWrapper.eq(interfaceInfoId != null, "interfaceId", interfaceInfoId);
        queryWrapper.eq(totalNum != null, "totalNum", totalNum);
        queryWrapper.eq(leftNum != null, "leftNum", leftNum);
        queryWrapper.eq(status != null, "status", status);
        return queryWrapper;
    }

    @Override
    public boolean invokeCount(long interfaceId, long userId) {

        // 接口调用统计
        if(userId <= 0 || interfaceId <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }

        // 使用UpdateWrapper来构建更新条件
        UpdateWrapper<UserInterfaceInfo> updateWrapper = new UpdateWrapper<>();
        // 跟新条件与传入的2个参数一致
        updateWrapper.eq("interfaceId", interfaceId);
        updateWrapper.eq("userId", userId);

        // setSql设置要更新的 SQL 语句 通过sql表达式来更新
        // 剩余次数 leftNum-1，累计使用次数 totalNum+1

        // 考虑 1.如果涉及 用户大量调用接口，避免出错 需要考虑 （事务和锁) 的知识
        //     2.事务保证原子性 锁要保证多线程同时修改一个数据的情况

        updateWrapper.setSql("leftNum = leftNum - 1, totalNum = totalNum + 1");
        // 最后调用 update方法 执行更新操作，并返回更新后的结果
        return this.update(updateWrapper);
    }

}




