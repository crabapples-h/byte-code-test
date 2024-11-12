package cn.crabapples;

import cn.crabapples.common.ResponseDTO;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.NamingStrategy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.matcher.ElementMatchers;

import java.io.File;
import java.util.Objects;

import static net.bytebuddy.matcher.ElementMatchers.any;
import static net.bytebuddy.matcher.ElementMatchers.named;

public class ByteBuddyExample {
    private static final String path = Objects.requireNonNull(ByteBuddyExample.class.getResource("")).getPath();
    private static final String NAME = "customName";
    private static final NamingStrategy.SuffixingRandom NAME_SUFFIXING = new NamingStrategy.SuffixingRandom(NAME);

    public static void main(String[] args) throws Exception {
        // 使用 ByteBuddy 创建带有注解的方法
        DynamicType.Unloaded<ControllerTemplate> unloaded = new ByteBuddy()
                .with(NAME_SUFFIXING)
                .redefine(ControllerTemplate.class)
//                .name("GeneratedClass")
                .name("cn.crabapples.TestSubController")
                .method(named("list"))
//                .intercept(FixedValue.value(new ResponseDTO<Object>("1", "hello")))
                .intercept(FixedValue.nullValue())
                .make();

        DynamicType.Loaded<ControllerTemplate> load = unloaded.load(ByteBuddyExample.class.getClassLoader());
//        Class<? extends ControllerTemplate> loaded = load.getLoaded();
//        Object controller = loaded.getDeclaredConstructor().newInstance();
        load.saveIn(new File(path));

//        System.err.println(controller);

        // 打印方法注解
//        System.out.println(dynamicType.getMethod("method1").getAnnotation(MyAnnotation1.class).value());
//        System.out.println(dynamicType.getMethod("method2").getAnnotation(MyAnnotation2.class).value());
    }
}
