package com.kai.entity;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author kai
 * @date 2021/8/27 下午10:27
 */

@Data
@ToString
public class QueryInfo implements Serializable {

    /**
     * 查询信息
     */
    private String query;
    /**
     * 当前页
     */
    private Integer pageNum;
    /**
     * 每页最大数
     */
    private Integer pageSize;

}
