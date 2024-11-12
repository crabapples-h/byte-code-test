package base;

import lombok.extern.slf4j.Slf4j;

/**
 * 方法委托(委托给实例方法)
 */
@Slf4j
public class MethodIntercept2 {
    public Object testMethod() {
        log.info("执行了MethodIntercept2[{}]方法", "testMethod()");
        return "hello world：MethodIntercept2";
    }
}
