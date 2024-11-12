package base;

import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.Callable;

/**
 * 方法委托(委托给带有@RuntimeType注解的方法)
 */
@Slf4j
public class MethodIntercept3 {
    @RuntimeType
    public Object handler(
            // 被拦截的目标对象,拦截实例方法或构造方法时可用
            @This Object targetObject,
            // 被拦截的目标方法,拦截实例方法和静态方法时可用
            @Origin Method targetMethod,
            // 被拦截的目标方法的参数
            @AllArguments Object[] args,
            // 被拦截的目标对象，拦截实例方法或构造方法时可用
            @Super Object targetObject2,

            // 用于调用原方法
            // 若确定父类，也可用具体的父类来接受  @SuperCall TestSuperClass zuper
            @SuperCall Callable<?> zuper) throws Exception {
        log.info("MethodIntercept6->\ntargetObject:[{}]\ntargetMethod:[{}]\nargs:[{}]\ntargetObject2:[{}]\nsuper:[{}]",
                targetObject, targetMethod, Arrays.toString(args), targetObject2, zuper);
        // 调用原方法
        return zuper.call();
    }
}
