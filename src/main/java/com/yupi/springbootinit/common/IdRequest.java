package com.yupi.springbootinit.common;

import java.io.Serializable;
import lombok.Data;

/**
 * 接口ID
 */
@Data
public class IdRequest implements Serializable{

    /**
     * id
     */
    private Long id;

    private static final long serialVersionUID = 1L;
}
