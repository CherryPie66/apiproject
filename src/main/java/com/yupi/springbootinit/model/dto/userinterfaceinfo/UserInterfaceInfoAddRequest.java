package com.yupi.springbootinit.model.dto.userinterfaceinfo;

import lombok.Data;

import java.io.Serializable;

/**
 * 创建用户接口信息请求
 */
@Data
public class UserInterfaceInfoAddRequest implements Serializable {

    /**
     * userId
     */
    private Long userId;

    /**
     * 接口Id
     */
    private Long interfaceId;

    /**
     * 总调用次数--累加使用次数
     */
    private Integer totalNum;

    /**
     * 剩余调用次数
     */
    private Integer leftNum;

}