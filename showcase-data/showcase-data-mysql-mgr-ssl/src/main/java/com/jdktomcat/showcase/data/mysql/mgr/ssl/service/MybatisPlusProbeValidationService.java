package com.jdktomcat.showcase.data.mysql.mgr.ssl.service;

import com.jdktomcat.showcase.data.mysql.mgr.ssl.entry.SslProbe;
import com.jdktomcat.showcase.data.mysql.mgr.ssl.mapper.SslProbeMapper;
import com.jdktomcat.showcase.data.mysql.mgr.ssl.model.MybatisPlusProbeResult;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public class MybatisPlusProbeValidationService {

    private final SslProbeMapper sslProbeMapper;
    private final JdbcTemplate jdbcTemplate;

    public MybatisPlusProbeValidationService(SslProbeMapper sslProbeMapper, JdbcTemplate jdbcTemplate) {
        this.sslProbeMapper = sslProbeMapper;
        this.jdbcTemplate = jdbcTemplate;
    }

    public MybatisPlusProbeResult validate() {
        try {
            SslProbe probe = sslProbeMapper.selectById(1L);
            if (probe == null) {
                return new MybatisPlusProbeResult(false, null, 1L, null, null, null, 0, "ssl_probe id=1 not found");
            }

            String routedHost = jdbcTemplate.queryForObject("SELECT @@hostname", String.class);
            String updatedDetail = "validated-by-mybatis-plus@" + OffsetDateTime.now();

            SslProbe update = new SslProbe();
            update.setId(1L);
            update.setDetail(updatedDetail);
            int updatedRows = sslProbeMapper.updateById(update);

            SslProbe refreshed = sslProbeMapper.selectById(1L);
            boolean valid = updatedRows > 0 && refreshed != null && updatedDetail.equals(refreshed.getDetail());

            return new MybatisPlusProbeResult(
                    valid,
                    routedHost,
                    probe.getId(),
                    probe.getLabel(),
                    probe.getDetail(),
                    refreshed == null ? null : refreshed.getDetail(),
                    updatedRows,
                    null
            );
        } catch (Exception ex) {
            return new MybatisPlusProbeResult(false, null, 1L, null, null, null, 0, ex.getMessage());
        }
    }
}
