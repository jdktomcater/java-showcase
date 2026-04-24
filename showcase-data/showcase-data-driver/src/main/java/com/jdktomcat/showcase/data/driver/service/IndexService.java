package com.jdktomcat.showcase.data.driver.service;

import com.jdktomcat.showcase.data.driver.domain.IndexVO;
import com.jdktomcat.showcase.data.driver.mapper.IndexMapper;
import com.jdktomcat.showcase.data.driver.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;


/**
 * 首页 数据字典
 *
 * @author jsbucky
 * @date 2020-02-08
 */

@Service
public class IndexService {

    @Autowired
    private IndexMapper indexMapper;

    public IndexVO getIndexorderBysucceed(IndexVO indexVO) {
        Long failcount = indexMapper.getIndexorderByfail(indexVO);
        Long totalcount = indexMapper.getIndexorderBytotal(indexVO);
        IndexVO resulstVO = indexMapper.getIndexorderBysucceed(indexVO);
        resulstVO.setFailcount(failcount);
        resulstVO.setTotalcount(totalcount);
        return resulstVO;
    }

    public IndexVO getIndexVOToday() {
        IndexVO indexVO = new IndexVO();
        indexVO.setQuerytime(DateUtils.getNowDateFromZero());
        return indexVO;
    }
}
