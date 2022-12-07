package com.evil.framework.core.alarm;

import com.evil.framework.core.autoconfigure.alarm.AlarmProperties;
import com.project.dingtalk.robot.send.RobotSendServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;

import static com.evil.framework.core.support.HostSupport.getHostName;
import static com.evil.framework.core.support.HostSupport.getIp;


public class DefaultAlarmHandler implements ApplicationAlarmHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultAlarmHandler.class);

    private static final String ENV_PROD = "prod";

    private static final String ALARM_CONTENT_TEMPLATE = "服务名称: **%s** \n" +
            "*环    境*: %s \n" +
            "*trace-id*: %s \n" +
            "*key*: %s \n" +
            "*host*: %s \n" +
            "*host-name*: %s \n" +
            "*exception*: %s";

    private final Environment environment;

    private final AlarmProperties alarmProperties;

    @Autowired
    private RobotSendServices robotSender;

    static {
        // 触发初始化
        getIp();
    }

    public DefaultAlarmHandler(Environment environment,
                               AlarmProperties alarmProperties) {
        this.environment = environment;
        this.alarmProperties = alarmProperties;
    }


    @Override
    public void handleException(String requestUri, String traceId, Throwable throwable) {

        String env = environment.getProperty("env", "");

        // 告警url
        String alarmUrl = alarmProperties.getUrl();

        // framework.alarm.url 配置不为空发送告警信息
        // 生产进行告警
        if (StringUtils.hasText(alarmUrl)) {

            // 应用名称
            String applicationName = environment.getProperty("spring.application.name", "");

            String alarmContent = String.format(
                    ALARM_CONTENT_TEMPLATE,
                    applicationName,
                    env,
                    traceId,
                    requestUri,
                    getIp(),
                    getHostName(),
                    exceptionToMsg(throwable)
            );

            StringBuilder sb = new StringBuilder();
            sb.append(alarmContent).append("\n");
            sb.append("\n");
            sb.append(getLinkText("日志平台", logUrl(applicationName, env, traceId, OffsetDateTime.now()))).append("\n");
            sb.append(getLinkText("APM", apmUrl(traceId))).append("\n");

            robotSender.sendMarkdownMessage(alarmUrl, "服务非预期异常告警", sb.toString(), new String[]{"all"});

        }
    }

    private static String exceptionToMsg(Throwable throwable) {
        if (Objects.isNull(throwable)) {
            return "";
        }
        StringWriter sw = new StringWriter();
        PrintWriter printWriter = new PrintWriter(sw, true);
        throwable.printStackTrace(printWriter);
        return sw.toString().substring(0, 800) + "\n" + "...";
    }

    public static String getLinkText(String text, String href) {
        return "[" + text + "](" + href + ")";
    }

    private String logUrl(String appName, String env, String keyword, OffsetDateTime occurredAt) {
        return "http://log.evil.cloud/static/r/#/search/index?appName=" + encode(appName) +
                "&env=" + encode(env) +
                "&keyword=" + encode(Optional.ofNullable(keyword).map(kw -> "\"" + kw + "\"").orElse("")) +
                "&start=" + encode(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(occurredAt.minusSeconds(1))) +
                "&end=" + encode(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(occurredAt.plusSeconds(1)));
    }

    private String apmUrl(String traceId) {
        return "https://op-skywalking-ui.evil.cloud/trace?traceid=" + Optional.ofNullable(traceId).orElse("");
    }

    private String encode(String text) {
        try {
            return URLEncoder.encode(text, "utf-8");
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("fail to encode text:{}", text, e);
        }
        return "";
    }

}
