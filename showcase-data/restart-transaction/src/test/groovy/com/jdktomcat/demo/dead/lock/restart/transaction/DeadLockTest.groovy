package com.jdktomcat.demo.dead.lock.restart.transaction

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders

import javax.servlet.http.HttpServletResponse
import spock.lang.Specification

@SpringBootTest
@AutoConfigureMockMvc
class DeadLockTest extends Specification {

    @Autowired
    private MockMvc mockMvc;

    def "dead lock test"() {
        given:

        when:
        def res = mockMvc.perform(MockMvcRequestBuilders.get("/one")).andReturn()
        then:
        res.response.status == HttpServletResponse.SC_OK
    }

}
