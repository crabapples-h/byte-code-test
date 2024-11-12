package base;

import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;

/**
 * 对构造方法插桩
 */
@Slf4j
public class MethodIntercept5 {
    @RuntimeType
    public void handler(@This Object targetObject) {
        log.info("执行了MethodIntercept5[{}]方法", "testMethod()");
    }
}
