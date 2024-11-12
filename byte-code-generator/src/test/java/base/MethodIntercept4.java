package base;

import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Morph;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;

/**
 * 动态修改入参
 */
@Slf4j
public class MethodIntercept4 {
    @RuntimeType
    public Object handler(
            // 被拦截的目标方法的参数
            @AllArguments Object[] args,
            // 使用@Morph动态修改入参
            @Morph MyCallable zuper) {
        if (args != null && args.length > 0) {
            log.info("执行了MethodIntercept4[{}]方法,原始入参:[{}]", "handler", args[0]);
            args[0] = (int) args[0] + 1;
            log.info("执行了MethodIntercept4[{}]方法,修改后后的参数:[{}]", "handler", args[0]);
        }
        // 调用父类的方法
        Object call = zuper.call(args);
        log.info("父类的方法的返回值:[{}]", call);
        return call;
    }
}
