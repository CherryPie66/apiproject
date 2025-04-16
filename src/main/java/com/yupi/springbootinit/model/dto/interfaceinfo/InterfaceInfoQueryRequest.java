package com.yupi.springbootinit.model.dto.interfaceinfo;

import com.yupi.springbootinit.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 查询接口信息请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class InterfaceInfoQueryRequest extends PageRequest implements Serializable {
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
     * 请求参数
     */
    private String requestParams;

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
     * 标签列表
     */
    private List<String> tags;
}