package com.evil.framework.web.logger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Constant about {@link LoggerFilter}
 *
 * @author  kongzheng
 * @since 1.0.0.RELEASE
 */
class LoggerConst {

    private static final String REQUEST_KEY_HOST = "host";
    private static final String REQUEST_KEY_CONTENT_TYPE = "content-type";
    private static final String REQUEST_KEY_CONTENT_LENGTH = "content-length";
    private static final String REQUEST_KEY_USER_AGENT = "user-agent";
    private static final String REQUEST_KEY_X_FORWARDED_FOR = "x-forwarded-for";

    static final String REQUEST_PREFIX = "^^A ";
    static final String KEY_VALUE_SEPARATOR = ": ";
    static final String RESPONSE_PREFIX = "^^A ";
    static final String VALUE_DEFAULT = "-";
    static final String REQUEST_IDENTITY = "====>request";
    static final String RESPONSE_IDENTITY = "<====response";
    static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    /**
     * request 记录的key
     */
    static final String REQUEST_KEY_REQUEST_TIME = "request-time";
    static final String REQUEST_KEY_EXTRA_PARAM = "extra-param";
    static final String REQUEST_KEY_BODY_PARAM = "body-param";
    static final String REQUEST_KEY_FORM_PARAM = "form-param";
    static final String REQUEST_KEY_URL = "url";
    static final String REQUEST_KEY_HTTP_METHOD = "http-method";

    /**
     * response 记录的key
     */
    static final String RESPONSE_KEY_RESPONSE_TIME = "response-time";
    static final String RESPONSE_KEY_HTTP_CODE = "http-code";
    static final String RESPONSE_KEY_CONTENT_TYPE = "content-type";
    static final String RESPONSE_KEY_TAKE_TIME = "take-time";
    static final String RESPONSE_KEY_RESPONSE_DATA = "response-data";

    static final List<String> REQUEST_KEY_LIST = new ArrayList<>();
    static final List<String> RESPONSE_KEY_LIST = new ArrayList<>();

    static {
        //初始化request中的key add的顺序即为日志显示的顺序
        REQUEST_KEY_LIST.add(LoggerConst.REQUEST_KEY_REQUEST_TIME);
        REQUEST_KEY_LIST.add(LoggerConst.REQUEST_KEY_URL);
        REQUEST_KEY_LIST.add(LoggerConst.REQUEST_KEY_HTTP_METHOD);
        REQUEST_KEY_LIST.add(LoggerConst.REQUEST_KEY_CONTENT_TYPE);
        REQUEST_KEY_LIST.add(LoggerConst.REQUEST_KEY_CONTENT_LENGTH);
        REQUEST_KEY_LIST.add(LoggerConst.REQUEST_KEY_HOST);
        REQUEST_KEY_LIST.add(LoggerConst.REQUEST_KEY_USER_AGENT);
        REQUEST_KEY_LIST.add(LoggerConst.REQUEST_KEY_X_FORWARDED_FOR);
        REQUEST_KEY_LIST.add(LoggerConst.REQUEST_KEY_EXTRA_PARAM);
        REQUEST_KEY_LIST.add(LoggerConst.REQUEST_KEY_BODY_PARAM);

        //初始化response中的key add的顺序即为日志显示的顺序
        RESPONSE_KEY_LIST.add(LoggerConst.RESPONSE_KEY_RESPONSE_TIME);
        RESPONSE_KEY_LIST.add(LoggerConst.RESPONSE_KEY_HTTP_CODE);
        RESPONSE_KEY_LIST.add(LoggerConst.RESPONSE_KEY_CONTENT_TYPE);
        RESPONSE_KEY_LIST.add(LoggerConst.RESPONSE_KEY_TAKE_TIME);
        RESPONSE_KEY_LIST.add(LoggerConst.RESPONSE_KEY_RESPONSE_DATA);

    }

}
