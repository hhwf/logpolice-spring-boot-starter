package com.logpolice.infrastructure.rpc;

import com.logpolice.domain.entity.ExceptionStatistic;
import com.logpolice.domain.repository.ExceptionStatisticRepository;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 异常统计本地缓存
 *
 * @author huang
 * @date 2019/9/11
 */
@Slf4j
public class ExceptionStatisticLocalCache implements ExceptionStatisticRepository {

    private Map<String, ExceptionStatistic> checkOpenId;

    public ExceptionStatisticLocalCache(Map<String, ExceptionStatistic> checkOpenId) {
        this.checkOpenId = checkOpenId;
    }

    @Override
    public Optional<ExceptionStatistic> findByOpenId(String openId) {
        ExceptionStatistic exceptionStatistic = checkOpenId.get(openId);
        if (Objects.isNull(exceptionStatistic)) {
            return Optional.empty();
        }
        return Optional.of(new ExceptionStatistic(exceptionStatistic.getShowCount(),
                exceptionStatistic.getOpenId(),
                exceptionStatistic.getFirstTime(),
                exceptionStatistic.getNoticeTime(),
                exceptionStatistic.getLastShowedCount()));
    }

    @Override
    public void save(String openId, ExceptionStatistic exceptionStatistic) {
        checkOpenId.put(openId, exceptionStatistic);
    }
}
