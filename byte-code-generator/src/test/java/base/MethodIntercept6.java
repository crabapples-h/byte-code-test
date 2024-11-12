package base;

import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.Callable;

/**
 * 对静态方法插桩处理
 */
@Slf4j
public class MethodIntercept6 {
    @RuntimeType
    public Object handler(
            // 被拦截的目标对象,拦截静态方法时可用
            @Origin Class<?> clazz,
            // 被拦截的目标方法,拦截实例方法和静态方法时可用
            @Origin Method targetMethod,
            // 被拦截的目标方法的参数
            @AllArguments Object[] args,
            // 用于调用原方法
            // 若确定父类，也可用具体的父类来接受  @SuperCall TestSuperClass zuper
            @SuperCall Callable<?> zuper) throws Exception {
        log.info("MethodIntercept6->\nclazz:[{}]\ntargetMethod:[{}]\nargs:[{}]\nsuper:[{}]",
                clazz, targetMethod, Arrays.toString(args), zuper);
        // 调用原方法
        return zuper.call();
    }
}
