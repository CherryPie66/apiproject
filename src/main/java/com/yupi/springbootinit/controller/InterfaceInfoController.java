package com.yupi.springbootinit.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cherry.apiclientsdk.client.ApiClient;
import com.google.gson.Gson;
import com.yupi.apicommon.model.entity.InterfaceInfo;
import com.yupi.apicommon.model.entity.User;
import com.yupi.springbootinit.annotation.AuthCheck;
import com.yupi.springbootinit.common.*;
import com.yupi.springbootinit.constant.UserConstant;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.exception.ThrowUtils;
import com.yupi.springbootinit.model.dto.interfaceinfo.InterfaceInfoAddRequest;
import com.yupi.springbootinit.model.dto.interfaceinfo.InterfaceInfoInvokeRequest;
import com.yupi.springbootinit.model.dto.interfaceinfo.InterfaceInfoQueryRequest;
import com.yupi.springbootinit.model.dto.interfaceinfo.InterfaceInfoUpdateRequest;
import com.yupi.springbootinit.model.enums.InterfaceStatusEnum;
import com.yupi.springbootinit.service.InterfaceInfoService;
import com.yupi.springbootinit.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * 接口信息管理
 */
@RestController
@RequestMapping("/interfaceInfo")
@Slf4j
public class InterfaceInfoController {

    @Resource
    private InterfaceInfoService interfaceInfoService;

    @Resource
    private UserService userService;

    @Resource
    private ApiClient apiClient;


    /**
     * 创建
     *
     * @param interfaceInfoAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addInterfaceInfo(@RequestBody InterfaceInfoAddRequest interfaceInfoAddRequest,
                                               HttpServletRequest request) {
        if (interfaceInfoAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoAddRequest, interfaceInfo);
        // 校验接口信息参数
        interfaceInfoService.validInterfaceInfo(interfaceInfo, true);
        // 用户信息
        User loginUser = userService.getLoginUser(request);
        interfaceInfo.setUserId(loginUser.getId());
        interfaceInfo.setUsername(loginUser.getUserName());

        interfaceInfo.setCreate_time(new Date());
        interfaceInfo.setUpdate_time(new Date());

        boolean result = interfaceInfoService.save(interfaceInfo);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newPostId = interfaceInfo.getId();
        return ResultUtils.success(newPostId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteInterfaceInfo(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        ThrowUtils.throwIf(oldInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = interfaceInfoService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param interfaceInfoUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateInterfaceInfo(@RequestBody InterfaceInfoUpdateRequest interfaceInfoUpdateRequest) {
        if (interfaceInfoUpdateRequest == null || interfaceInfoUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoUpdateRequest, interfaceInfo);
        // 参数校验
        interfaceInfoService.validInterfaceInfo(interfaceInfo, false);
        long id = interfaceInfoUpdateRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        ThrowUtils.throwIf(oldInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = interfaceInfoService.updateById(interfaceInfo);
        return ResultUtils.success(result);
    }

    /**
     * 发布（仅管理员）
     *
     * @param idRequest
     * @return
     */
    @PostMapping("/online")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> onlineInterfaceInfo(@RequestBody IdRequest idRequest) {
        if (idRequest == null || idRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 1.检验接口是否存在
        Long id = idRequest.getId();
        // 通过id找到接口对象
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if(oldInterfaceInfo == null){  // 没有找到对应的接口信息
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 2.判断接口是否可以调用（需要引入开发的sdk包）
        // todo 先用接口名调用下，后期补充
        com.cherry.apiclientsdk.model.User sdkUser = new com.cherry.apiclientsdk.model.User();
        sdkUser.setUserName("yupi");
        String userNameByPost = apiClient.getUserNameByPost(sdkUser);
        if(StringUtils.isBlank(userNameByPost)){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "接口调用验证失败");
        }

        // 验证通过
        InterfaceInfo userInterfaceInfo = new InterfaceInfo();
        userInterfaceInfo.setId(id);
        // 3.更新接口的状态（1-开启）
        userInterfaceInfo.setStatus(InterfaceStatusEnum.ONLINE.getValue());
        // 调用interfaceInfoService的更新方法
        boolean result = interfaceInfoService.updateById(userInterfaceInfo);
        return ResultUtils.success(result);
    }

    /**
     * 下线（仅管理员）AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
     *
     * @param idRequest
     * @return
     */
    @PostMapping("/offline")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> offlineInterfaceInfo(@RequestBody IdRequest idRequest) {
        if (idRequest == null || idRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 1.检验接口是否存在
        Long id = idRequest.getId();
        // 通过id找到接口对象
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if(oldInterfaceInfo == null){  // 没有找到对应的接口信息
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 2.判断接口是否可以调用（需要引入开发的sdk包）
        // todo 先用接口名调用下，后期补充
        com.cherry.apiclientsdk.model.User sdkUser = new com.cherry.apiclientsdk.model.User();
        sdkUser.setUserName("yupi");
        String userNameByPost = apiClient.getUserNameByPost(sdkUser);
        if(StringUtils.isBlank(userNameByPost)){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "接口调用验证失败");
        }

        // 验证通过
        InterfaceInfo userInterfaceInfo = new InterfaceInfo();
        userInterfaceInfo.setId(id);
        // 3.更新接口的状态（0-下线）
        userInterfaceInfo.setStatus(InterfaceStatusEnum.OFFLINE.getValue());
        // 调用interfaceInfoService的更新方法
        boolean result = interfaceInfoService.updateById(userInterfaceInfo);
        return ResultUtils.success(result);
    }

    /**
     * 接口测试调用
     *
     * @param interfaceInfoInvokeRequest
     * @return
     */
    @PostMapping("/invoke")
    public BaseResponse<Object> invokeInterfaceInfo(@RequestBody InterfaceInfoInvokeRequest interfaceInfoInvokeRequest,
                                                     HttpServletRequest request) {
        if (interfaceInfoInvokeRequest == null || interfaceInfoInvokeRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 1.接口信息 (id, 请求参数)
        // 获取接口id
        Long id = interfaceInfoInvokeRequest.getId();
        // 获取用户请求参数
        String userRequestParams = interfaceInfoInvokeRequest.getUserRequestParams();

        // 通过id找到接口对象
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if(oldInterfaceInfo == null){  // 没有找到对应的接口信息
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 检查接口是否可用（上线状态）
        if(oldInterfaceInfo.getStatus() != InterfaceStatusEnum.ONLINE.getValue()){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "接口不是发布状态！");
        }

        // 获取当前用户对象 ,以及用户的ak，sk
        User loginUser = userService.getLoginUser(request);
        String accessKey = loginUser.getAccessKey();
        String secretKey = loginUser.getSecretKey();

        // 测试接口调用
        // 创建临时的ApiClinet ,传入ak，sk
        ApiClient client = new ApiClient(accessKey, secretKey);
        Gson gson = new Gson();
        //
        com.cherry.apiclientsdk.model.User sdkUser =
                gson.fromJson(userRequestParams, com.cherry.apiclientsdk.model.User.class);
        String userNameByPost = client.getUserNameByPost(sdkUser);
        if(StringUtils.isBlank(userNameByPost)){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "接口调用验证失败");
        }
        return ResultUtils.success(userNameByPost);
    }


    /**
     * 接口测试调用 love-talk
     *
     * @param interfaceInfoInvokeRequest
     * @return
     */
    @PostMapping("/invoke/love")
    public BaseResponse<Object> invokeInterfaceInfoGetLoveTalk(@RequestBody InterfaceInfoInvokeRequest interfaceInfoInvokeRequest,
                                                    HttpServletRequest request) {
        if (interfaceInfoInvokeRequest == null || interfaceInfoInvokeRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 1.接口信息 (id, 请求参数)
        // 获取接口id
        Long id = interfaceInfoInvokeRequest.getId();
        // 获取用户请求参数
        String userRequestParams = interfaceInfoInvokeRequest.getUserRequestParams();

        // 通过id找到接口对象
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if(oldInterfaceInfo == null){  // 没有找到对应的接口信息
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 检查接口是否可用（上线状态）
        if(oldInterfaceInfo.getStatus() != InterfaceStatusEnum.ONLINE.getValue()){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "接口不是发布状态！");
        }

        // 获取当前用户对象 ,以及用户的ak，sk
        User loginUser = userService.getLoginUser(request);
        String accessKey = loginUser.getAccessKey();
        String secretKey = loginUser.getSecretKey();
        // 创建临时的ApiClient ,传入ak，sk
        ApiClient client = new ApiClient(accessKey, secretKey);
        try {
            // 调用土味情话的模拟接口

            String loveTalk = client.getLoveTalk();
            return ResultUtils.success(loveTalk);
        } catch (Exception e){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "情话获取失败");
        }

    }


//    /**
//     * 根据 id 获取
//     *
//     * @param id
//     * @return
//     */
//    @GetMapping("/get/vo")
//    public BaseResponse<InterfaceInfoVO> getInterfaceInfoVOById(long id, HttpServletRequest request) {
//        if (id <= 0) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        InterfaceInfo interfaceInfo = interfaceInfoService.getById(id);
//        if (userInterfaceInfo == null) {
//            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
//        }
//        return ResultUtils.success(interfaceInfoService.getInterfaceInfoVO(userInterfaceInfo, request));
//    }

    /**
     * 分页获取列表（仅管理员）
     *
     * @param interfaceInfoQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<InterfaceInfo>> listInterfaceInfoByPage(@RequestBody InterfaceInfoQueryRequest interfaceInfoQueryRequest) {
        long current = interfaceInfoQueryRequest.getCurrent();
        long size = interfaceInfoQueryRequest.getPageSize();
        Page<InterfaceInfo> userInterfaceInfoPage = interfaceInfoService.page(new Page<>(current, size),
                interfaceInfoService.getQueryWrapper(interfaceInfoQueryRequest));
        return ResultUtils.success(userInterfaceInfoPage);
    }

//    /**
//     * 分页获取列表（封装类）
//     *
//     * @param userInterfaceInfoQueryRequest
//     * @param request
//     * @return
//     */
//    @PostMapping("/list/page/vo")
//    public BaseResponse<Page<InterfaceInfoVO>> listInterfaceInfoVOByPage(@RequestBody InterfaceInfoQueryRequest userInterfaceInfoQueryRequest,
//            HttpServletRequest request) {
//        long current = userInterfaceInfoQueryRequest.getCurrent();
//        long size = userInterfaceInfoQueryRequest.getPageSize();
//        // 限制爬虫
//        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
//        Page<InterfaceInfoQueryRequest> userInterfaceInfoPage = interfaceInfoService.page(new Page<>(current, size),
//                interfaceInfoService.getQueryWrapper(userInterfaceInfoQueryRequest));
//        return ResultUtils.success(interfaceInfoService.getInterfaceInfoVOPage(userInterfaceInfoPage, request));
//    }

//    /**
//     * 分页获取当前用户创建的资源列表
//     *
//     * @param userInterfaceInfoQueryRequest
//     * @param request
//     * @return
//     */
//    @PostMapping("/my/list/page/vo")
//    public BaseResponse<Page<InterfaceInfoVO>> listMyInterfaceInfoVOByPage(@RequestBody InterfaceInfoQueryRequest userInterfaceInfoQueryRequest,
//            HttpServletRequest request) {
//        if (userInterfaceInfoQueryRequest == null) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        User loginUser = userService.getLoginUser(request);
//        userInterfaceInfoQueryRequest.setUserId(loginUser.getId());
//        long current = userInterfaceInfoQueryRequest.getCurrent();
//        long size = userInterfaceInfoQueryRequest.getPageSize();
//        // 限制爬虫
//        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
//        Page<InterfaceInfo> userInterfaceInfoPage = interfaceInfoService.page(new Page<>(current, size),
//                interfaceInfoService.getQueryWrapper(userInterfaceInfoQueryRequest));
//        return ResultUtils.success(interfaceInfoService.getInterfaceInfoVOPage(userInterfaceInfoPage, request));
//    }

    // endregion

//    /**
//     * 分页搜索（从 ES 查询，封装类）
//     *
//     * @param userInterfaceInfoQueryRequest
//     * @param request
//     * @return
//     */
//    @PostMapping("/search/page/vo")
//    public BaseResponse<Page<InterfaceInfoVO>> searchPostVOByPage(@RequestBody InterfaceInfoQueryRequest userInterfaceInfoQueryRequest,
//            HttpServletRequest request) {
//        long size = userInterfaceInfoQueryRequest.getPageSize();
//        // 限制爬虫
//        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
//        Page<InterfaceInfo> userInterfaceInfoPage = interfaceInfoService.searchFromEs(userInterfaceInfoQueryRequest);
//        return ResultUtils.success(interfaceInfoService.getInterfaceInfoVOPage(userInterfaceInfoPage, request));
//    }

//    /**
//     * 编辑（用户）
//     *
//     * @param userInterfaceInfoEditRequest
//     * @param request
//     * @return
//     */
//    @PostMapping("/edit")
//    public BaseResponse<Boolean> editInterfaceInfo(@RequestBody InterfaceInfoEditRequest userInterfaceInfoEditRequest, HttpServletRequest request) {
//        if (userInterfaceInfoEditRequest == null || userInterfaceInfoEditRequest.getId() <= 0) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        InterfaceInfo userInterfaceInfo = new InterfaceInfo();
//        BeanUtils.copyProperties(userInterfaceInfoEditRequest, userInterfaceInfo);
//        List<String> tags = userInterfaceInfoEditRequest.getTags();
//        if (tags != null) {
//            userInterfaceInfo.setTags(JSONUtil.toJsonStr(tags));
//        }
//        // 参数校验
//        interfaceInfoService.validInterfaceInfo(userInterfaceInfo, false);
//        User loginUser = userService.getLoginUser(request);
//        long id = userInterfaceInfoEditRequest.getId();
//        // 判断是否存在
//        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
//        ThrowUtils.throwIf(oldInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
//        // 仅本人或管理员可编辑
//        if (!oldInterfaceInfo.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
//            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
//        }
//        boolean result = interfaceInfoService.updateById(userInterfaceInfo);
//        return ResultUtils.success(result);
//    }

}
