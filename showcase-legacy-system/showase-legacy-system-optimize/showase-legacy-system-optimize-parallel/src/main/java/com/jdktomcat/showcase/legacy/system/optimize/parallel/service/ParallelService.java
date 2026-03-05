package com.jdktomcat.showcase.legacy.system.optimize.parallel.service;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParallelService {



    /**
     * serial handle
     *
     * @param ids target id list
     * @return result
     */
    public List<String> serial(List<Long> ids){
        return null;
    }

    /**
     * parallel handle
     *
     * @param ids target id list
     * @return result
     */
    public List<String> parallel(List<Long> ids){
        return null;
    }
}
