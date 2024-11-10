package cn.crabapples.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@Component
@ControllerAdvice
public class CustomExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(CustomExceptionHandler.class);

    @ResponseBody
    @ExceptionHandler
    protected ResponseDTO<Object> applicationExceptionHandler(Exception e) {
        logger.warn("XHR出现异常:[{}]", e.getMessage(), e);
        if (e instanceof HttpMessageNotReadableException) {
            return new ResponseDTO<>().returnError(null,"参数错误");
        }
        if (e instanceof ApplicationException) {
            if (401 == ((ApplicationException) e).getCode()) {
                return new ResponseDTO<>().returnAuthFail(null,"身份认证失败");
            }
        }
        return new ResponseDTO<>().returnError(null,"操作失败:" + e.getMessage());
    }
}
