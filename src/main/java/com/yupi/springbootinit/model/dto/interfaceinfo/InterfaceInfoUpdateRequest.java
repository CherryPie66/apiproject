package com.yupi.springbootinit.model.dto.interfaceinfo;

import lombok.Data;

import java.io.Serializable;

/**
 * 更新接口信息
 */
@Data
public class InterfaceInfoUpdateRequest implements Serializable {

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
}