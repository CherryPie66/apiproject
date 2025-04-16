package com.yupi.springbootinit.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.apicommon.model.entity.InterfaceInfo;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.constant.CommonConstant;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.exception.ThrowUtils;
import com.yupi.springbootinit.model.dto.interfaceinfo.InterfaceInfoQueryRequest;
import com.yupi.springbootinit.mapper.InterfaceInfoMapper;
import com.yupi.springbootinit.service.InterfaceInfoService;
import com.yupi.springbootinit.utils.SqlUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author zzp18
* @description 针对表【interface_info(接口信息)】的数据库操作Service实现
* @createDate 2024-03-31 17:20:47
*/
@Service
public class InterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper, InterfaceInfo>
    implements InterfaceInfoService{


    @Override
    public void validInterfaceInfo(InterfaceInfo userInterfaceInfo, boolean add) {
        if (userInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String interfaceName = userInterfaceInfo.getInterface_name();
        String url = userInterfaceInfo.getUrl();
        String method = userInterfaceInfo.getType();
        String requestHead = userInterfaceInfo.getRequest_head();
        String reponseHead = userInterfaceInfo.getReponse_head();

        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(interfaceName, url, method, requestHead, reponseHead), ErrorCode.PARAMS_ERROR);
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(interfaceName) && interfaceName.length() > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "名称过长");
        }
        if (StringUtils.isNotBlank(url) && url.length() > 100) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口地址不存在");
        }
        if (StringUtils.isNotBlank(method) && method.length() > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口方法不存在");
        }
        if (StringUtils.isNotBlank(requestHead) && requestHead.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求头过长");
        }
        if (StringUtils.isNotBlank(reponseHead) && reponseHead.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "响应头过长");
        }
    }

    /**
     * 获取查询包装类
     *
     * @param userInterfaceInfoQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<InterfaceInfo> getQueryWrapper(InterfaceInfoQueryRequest userInterfaceInfoQueryRequest) {
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
        if (userInterfaceInfoQueryRequest == null) {
            return queryWrapper;
        }
        String sortField = userInterfaceInfoQueryRequest.getSortField();
        String sortOrder = userInterfaceInfoQueryRequest.getSortOrder();

        Long id = userInterfaceInfoQueryRequest.getId();
        // 接口信息
        String interfaceName = userInterfaceInfoQueryRequest.getInterface_name();
        String description = userInterfaceInfoQueryRequest.getDescription();
        String url = userInterfaceInfoQueryRequest.getUrl();

        List<String> tagList = userInterfaceInfoQueryRequest.getTags();
        Long userId = userInterfaceInfoQueryRequest.getUserId();

        queryWrapper.like(StringUtils.isNotBlank(interfaceName), "interfaceName", interfaceName);
        queryWrapper.like(StringUtils.isNotBlank(description), "description", description);
        queryWrapper.like(StringUtils.isNotBlank(url), "url", url);

        if (CollUtil.isNotEmpty(tagList)) {
            for (String tag : tagList) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

}




