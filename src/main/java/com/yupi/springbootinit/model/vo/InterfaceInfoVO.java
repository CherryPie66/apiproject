package com.yupi.springbootinit.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 接口信息视图
 *


 */
@Data
public class InterfaceInfoVO implements Serializable {

    /**
     * 主键
     */
    private Long id;

    /**
     * 接口名
     */
    private String interface_name;

    /**
     * 接口地址
     */
    private String url;

    /**
     * 接口描述
     */
    private String description;

    /**
     * 接口类型
     */
    private String type;

    /**
     * 请求头
     */
    private String request_head;

    /**
     * 响应头
     */
    private String reponse_head;

    /**
     * 接口状态（0-关闭，1-开启）
     */
    private Integer status;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 创建用户名
     */
    private String username;

    /**
     * 创建时间
     */
    private Date create_time;

    /**
     * 更新时间
     */
    private Date update_time;

    /**
     * 标签列表
     */
    private List<String> tagList;

//    /**
//     * 包装类转对象
//     *
//     * @param userInterfaceInfoVO
//     * @return
//     */
//    public static InterfaceInfo voToObj(InterfaceInfoVO userInterfaceInfoVO) {
//        if (userInterfaceInfoVO == null) {
//            return null;
//        }
//        InterfaceInfo userInterfaceInfo = new InterfaceInfo();
//        BeanUtils.copyProperties(userInterfaceInfoVO, userInterfaceInfo);
//        List<String> tagList = userInterfaceInfoVO.getTagList();
//        userInterfaceInfo.setTags(JSONUtil.toJsonStr(tagList));
//        return userInterfaceInfo;
//    }

//    /**
//     * 对象转包装类
//     *
//     * @param userInterfaceInfo
//     * @return
//     */
//    public static InterfaceInfoVO objToVo(InterfaceInfo userInterfaceInfo) {
//        if (userInterfaceInfo == null) {
//            return null;
//        }
//        InterfaceInfoVO userInterfaceInfoVO = new InterfaceInfoVO();
//        BeanUtils.copyProperties(userInterfaceInfo, userInterfaceInfoVO);
//        userInterfaceInfoVO.setTagList(JSONUtil.toList(userInterfaceInfo.getTags(), String.class));
//        return userInterfaceInfoVO;
//    }
}
