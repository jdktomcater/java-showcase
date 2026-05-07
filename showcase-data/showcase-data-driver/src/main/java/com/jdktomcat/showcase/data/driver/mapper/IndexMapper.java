package com.jdktomcat.showcase.data.driver.mapper;


import com.jdktomcat.showcase.data.driver.domain.IndexVO;


/**
 * 订单字典apper接口
 */
public interface IndexMapper {

    IndexVO getIndexorderBysucceed(IndexVO indexVO);

    Long getIndexorderByfail(IndexVO indexVO);

    Long getIndexorderBytotal(IndexVO indexVO);

    void sleep();
}
