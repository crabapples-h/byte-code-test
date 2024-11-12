package base;

import lombok.extern.slf4j.Slf4j;

/**
 * 方法委托(委托给静态方法)
 */
@Slf4j
public class MethodIntercept1 {
    public static Object testMethod() {
        log.info("执行了MethodIntercept1[{}]方法", "testMethod()");
        return "hello world：MethodIntercept1";
    }
}
