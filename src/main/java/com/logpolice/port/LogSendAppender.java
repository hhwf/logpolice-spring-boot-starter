package com.logpolice.port;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import com.logpolice.application.NoticeService;
import com.logpolice.domain.entity.ExceptionNotice;
import com.logpolice.infrastructure.properties.LogpoliceConstant;
import com.logpolice.infrastructure.properties.LogpoliceProperties;
import com.logpolice.infrastructure.utils.ApplicationContextProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.Set;

/**
 * 日志报警Appender
 *
 * @author huang
 * @date 2019/8/27
 */
@Slf4j
public class LogSendAppender extends UnsynchronizedAppenderBase<LoggingEvent> {

    private PatternLayout layout;

    @Override
    public void start() {
        PatternLayout patternLayout = new PatternLayout();
        patternLayout.setContext(context);
        patternLayout.setPattern(LogpoliceConstant.PROFILES_ACTIVE);
        patternLayout.start();
        this.layout = patternLayout;
        super.start();
    }

    @Override
    protected void append(LoggingEvent eventObject) {
        if (Level.ERROR.equals(eventObject.getLevel())) {
            LogpoliceProperties logpoliceProperties = ApplicationContextProvider.getBean(LogpoliceProperties.class);
            if (!logpoliceProperties.getEnabled()) {
                return;
            }

            ExceptionNotice exceptionNotice = new ExceptionNotice(logpoliceProperties.getAppCode(),
                    logpoliceProperties.getLocalIp(), layout.doLayout(eventObject), eventObject);

            Set<String> exceptionWhiteList = logpoliceProperties.getExceptionWhiteList();
            if (!CollectionUtils.isEmpty(exceptionWhiteList) && exceptionWhiteList.contains(exceptionNotice.getExceptionClassName())) {
                log.info("logSendAppender.append exceptionWhiteList skip send, exception:{}", exceptionNotice.getExceptionClassName());
                return;
            }

            NoticeService noticeService = ApplicationContextProvider.getBean(NoticeService.class);
            noticeService.send(exceptionNotice, logpoliceProperties);
        }
    }
}