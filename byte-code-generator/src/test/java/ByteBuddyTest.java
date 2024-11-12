import base.*;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.NamingStrategy;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.SuperMethodCall;
import net.bytebuddy.implementation.bind.annotation.Morph;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Objects;

import static net.bytebuddy.implementation.MethodDelegation.to;
import static net.bytebuddy.matcher.ElementMatchers.*;

@Slf4j
public class ByteBuddyTest {
    private String path;
    private static final String NAME = "customName";

    @Before
    public void before() {
        path = Objects.requireNonNull(this.getClass().getResource("")).getPath();
    }

    /**
     * 生成一个类
     */
    @Test
    public void caseTest1() throws IOException {
        NamingStrategy.SuffixingRandom suffixingRandom = new NamingStrategy.SuffixingRandom(NAME);
        DynamicType.Unloaded<TestSuperClass> unloaded = new ByteBuddy()
                // 关闭生成的字节码进行合法校验
//                .with(TypeValidation.of(false))
                /*
                 * 默认生成的类的命名规则
                 * 在不指定命名策略的情况下：
                 *  1. 对于父类是jdk自带的情况下生成的类名为
                 *      net.bytebuddy.renamed.java.Lang.Object$ByteBuddy$xxxx
                 *  2. 对于父类不是jdk自带情况下的类，则类名为
                 *      当前包名$父类名$xxxx
                 *  在指定命名了策略的情况下：
                 *  1. 对于父类是jdk自带的情况下生成的类名为
                 *      net.bytebuddy.renamed.java.Lang.Object$customName$xxxx
                 *  2. 对于父类不是jdk自带情况下的类，则类名为
                 *      当前包名$customName$xxxx
                 */
                // 指定生成的类的命名策略
                .with(suffixingRandom)
                // 指定父类
                .subclass(TestSuperClass.class)
                // 指定生成的类的全类名
                .name("cn.crabapples.TestSubClass")
                .make();
        // 生成字节码
        byte[] bytes = unloaded.getBytes();
//        FileUtils.writeByteArrayToFile(new File(path + "base.TestSuperClass.class"), bytes);
        // 把生成的字节码文件直接写入到本地
//        FileUtils.writeByteArrayToFile(new File(path +"TestSubClass.class"), bytes);
        unloaded.saveIn(new File(path));
        log.info("生成字节码文件路径：{}", path);
        // 把生成的字节码文件直接注入到Jar包里
//        unloaded.inject(new File(path + "test.jar"));
    }

    /**
     * 对实例方法进行插桩
     * 插桩: 对字节码的修改或者增强
     */
    @Test
    public void caseTest2() throws IOException, InstantiationException, IllegalAccessException {
        NamingStrategy.SuffixingRandom test = new NamingStrategy.SuffixingRandom(NAME);
        DynamicType.Unloaded<TestSuperClass> unloaded = new ByteBuddy()
                .with(test)
                .subclass(TestSuperClass.class)
                .name("cn.crabapples.TestSubClass")
                // named通过名字指定需要拦截的方法
                .method(named("toString"))
                // 指定拦截到方法后的处理方法
                .intercept(FixedValue.value("hello kitty"))
                .make();
        // unloaded中有的方法在loaded中也有相同的方法
        DynamicType.Loaded<TestSuperClass> loaded = unloaded.load(getClass().getClassLoader());
        ClassLoader classLoader = loaded.getClass().getClassLoader();
        log.info("类加载器：[{}]", classLoader);
        TestSuperClass testSuperClass = loaded.getLoaded().newInstance();
        // 直接打印对象默认会调用对象的toString()方法，所以会打印hello kitty
        log.info("实例化对象：[{}]", testSuperClass);
        unloaded.saveIn(new File(path));
    }

    /**
     * 动态增强的三种方式
     * subclass
     */
    @Test
    public void caseTest3() throws IOException, InstantiationException, IllegalAccessException {
        NamingStrategy.SuffixingRandom test = new NamingStrategy.SuffixingRandom(NAME);
        DynamicType.Unloaded<TestSuperClass> unloaded = new ByteBuddy()
                .with(test)
                .subclass(TestSuperClass.class)
                .name("cn.crabapples.TestSubClass")
                // named通过名字指定需要拦截的方法
                .method(named("selectUserName")
                        // named通过返回值类型拦截
                        .and(returns(TypeDescription.CLASS)
                                .or(returns(TypeDescription.OBJECT))
                                .or(returns(TypeDescription.ForLoadedType.of(String.class)))
                        )
                )
                // 指定拦截到方法后的处理方法
                .intercept(FixedValue.value("被拦截修改后的值"))
                .make();
        // unloaded中有的方法在loaded中也有相同的方法
        DynamicType.Loaded<TestSuperClass> loaded = unloaded.load(getClass().getClassLoader());
        ClassLoader classLoader = loaded.getClass().getClassLoader();
        log.info("类加载器：[{}]", classLoader);
        TestSuperClass instance = loaded.getLoaded().newInstance();
        String username = instance.selectUserName("123");
        log.info("方法调用结果：[{}]", username);

        int age = instance.selectAge(123);
        log.info("方法调用结果：[{}]", age);
        unloaded.saveIn(new File(path));
    }

    /**
     * 动态增强的三种方式
     * rebase 变基。保留原方法，并重命名为xx$original(),xx为拦截后的逻辑
     */
    @Test
    public void caseTest4() throws IOException {
        DynamicType.Unloaded<TestSuperClass> unloaded = new ByteBuddy()
                .rebase(TestSuperClass.class)
                .name("cn.crabapples.TestSubClass")
                // named通过名字指定需要拦截的方法
                .method(named("selectAge"))
                // 指定拦截到方法后的处理方法
                .intercept(FixedValue.value(-1))
                .make();
        unloaded.saveIn(new File(path));
    }

    /**
     * 动态增强的三种方式
     * redefine 重定义。原方法不再保留，新方法为拦截后的逻辑
     */
    @Test
    public void caseTest5() throws IOException {
        DynamicType.Unloaded<TestSuperClass> unloaded = new ByteBuddy()
                .redefine(TestSuperClass.class)
                .name("cn.crabapples.TestSubClass")
                // named通过名字指定需要拦截的方法
                .method(named("selectAge"))
                // 指定拦截到方法后的处理方法
                .intercept(FixedValue.value(-1))
                .make();
        unloaded.saveIn(new File(path));
    }

    /**
     * 插入新方法
     */
    @Test
    public void caseTest6() throws IOException {
        DynamicType.Unloaded<TestSuperClass> unloaded = new ByteBuddy()
                .rebase(TestSuperClass.class)
                .name("cn.crabapples.TestSubClass")
                // 插入新方法 方法名,返回值类型,限定词
                .defineMethod("insertMethod1",
                        TypeDescription.ForLoadedType.of(String.class),
                        Modifier.PUBLIC + Modifier.STATIC)
                // 指定方法参数 参数类型,参数名
                .withParameter(TypeDescription.ForLoadedType.of(String.class), "arg1")
                .withParameter(TypeDescription.ForLoadedType.of(String.class), "arg2")
                // 返回值
                .intercept(FixedValue.value("拦截后添加的新方法"))
                .make();
        unloaded.saveIn(new File(path));
    }

    /**
     * 插入新属性
     */
    @Test
    public void caseTest7() throws IOException {
        DynamicType.Unloaded<TestSuperClass> unloaded = new ByteBuddy()
                .rebase(TestSuperClass.class)
                .name("cn.crabapples.TestSubClass")
                // 插入新属性 属性名,类型,限定词
                .defineField("insertField1", TypeDescription.ForLoadedType.of(String.class),
                        Modifier.PRIVATE)
                // 定义get,set方法,实现接口
                .implement(TestFieldInterface.class)
                .intercept(FieldAccessor.ofField("insertField1"))
                .make();
        unloaded.saveIn(new File(path));
    }
    /**
     * 方法委托(委托给静态方法)
     */
    @Test
    public void caseTest8() throws IOException {
        DynamicType.Unloaded<TestSuperClass> unloaded = new ByteBuddy()
                .subclass(TestSuperClass.class)
                .name("cn.crabapples.TestSubClass")
                .method(named("testMethod"))
                // 把处理逻辑委托给MethodIntercept1中与被拦截方法同签名的静态方法
                .intercept(to(MethodIntercept1.class))
                .make();
        unloaded.saveIn(new File(path));
    }

    /**
     * 方法委托(委托给实例方法)
     */
    @Test
    public void caseTest9() throws IOException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        DynamicType.Unloaded<TestSuperClass> unloaded = new ByteBuddy()
                .subclass(TestSuperClass.class)
                .name("cn.crabapples.TestSubClass")
                .method(named("testMethod"))
                // 把处理逻辑委托给MethodIntercept2中与被拦截方法同签名的实例方法
                .intercept(to(new MethodIntercept2()))
                .make();
        DynamicType.Loaded<TestSuperClass> load = unloaded.load(getClass().getClassLoader());
        Class<? extends TestSuperClass> loaded = load.getLoaded();
        TestSuperClass testSuperClass = loaded.getDeclaredConstructor().newInstance();
        testSuperClass.testMethod();
        load.saveIn(new File(path));
    }

    /**
     * 方法委托(委托给带有@RuntimeType注解的方法)
     */
    @Test
    public void caseTest10() throws IOException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        DynamicType.Unloaded<TestSuperClass> unloaded = new ByteBuddy()
                .subclass(TestSuperClass.class)
                .name("cn.crabapples.TestSubClass")
                .method(named("testMethod"))
                // 把处理逻辑委托给MethodIntercept3中带有@RuntimeType的方法
                .intercept(to(new MethodIntercept3()))
                .make();
        DynamicType.Loaded<TestSuperClass> load = unloaded.load(getClass().getClassLoader());
        Class<? extends TestSuperClass> loaded = load.getLoaded();
        TestSuperClass testSuperClass = loaded.getDeclaredConstructor().newInstance();
        Object o = testSuperClass.testMethod();
        System.err.println(o);
        load.saveIn(new File(path));
    }

    /**
     * 动态修改入参
     * 1. 自定义MyCallable
     * 2. 使用@Morph动态修改入参
     * 3. 指定拦截器前需要调用MethodDelegation.withDefaultConfiguration().withBinders()指定参数类型
     */
    @Test
    public void caseTest11() throws IOException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        DynamicType.Unloaded<TestSuperClass> unloaded = new ByteBuddy()
                .subclass(TestSuperClass.class)
                .name("cn.crabapples.TestSubClass")
                .method(named("testMethod1"))
                // 把处理逻辑委托给MethodIntercept4中带有@RuntimeType的方法
                .intercept(MethodDelegation.withDefaultConfiguration()
                        // 在MethodIntercept4使用MyCallable之前需要告诉ByteBuddy参数类型是MyCallable
                        .withBinders(Morph.Binder.install(MyCallable.class))
                        .to(new MethodIntercept4()))
                .make();
        DynamicType.Loaded<TestSuperClass> load = unloaded.load(getClass().getClassLoader());
        Class<? extends TestSuperClass> loaded = load.getLoaded();
        TestSuperClass testSuperClass = loaded.getDeclaredConstructor().newInstance();
        Integer o = testSuperClass.testMethod1(10);
        System.err.println(o);
        load.saveIn(new File(path));
    }

    /**
     * 对构造方法插桩
     */
    @Test
    public void caseTest12() throws IOException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        DynamicType.Unloaded<TestSuperClass> unloaded = new ByteBuddy()
                .subclass(TestSuperClass.class)
                .name("cn.crabapples.TestSubClass")
                // 拦截构造方法
                .constructor(any())
                // 把处理逻辑委托给MethodIntercept5中带有@RuntimeType的方法
                .intercept(
                        // 指定在构造方法在完成之后再委托给拦截器
                        SuperMethodCall.INSTANCE.andThen(MethodDelegation.to(new MethodIntercept5()))
                )
                .make();
        DynamicType.Loaded<TestSuperClass> load = unloaded.load(getClass().getClassLoader());
        Class<? extends TestSuperClass> loaded = load.getLoaded();
        TestSuperClass testSuperClass = loaded.getDeclaredConstructor().newInstance();
        Object o = testSuperClass.testMethod1(10);
        System.err.println(o);
        load.saveIn(new File(path));
    }

    /**
     * 对静态方法插桩
     */
    @Test
    public void caseTest13() throws IOException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        DynamicType.Unloaded<TestSuperClass> unloaded = new ByteBuddy()
                .rebase(TestSuperClass.class)
                .name("cn.crabapples.TestSubClass")
                // 拦截静态方法
                .method(named("testStaticMethod").and(isStatic()))
                // 把处理逻辑委托给MethodIntercept6中带有@RuntimeType的方法
                .intercept(MethodDelegation.to(new MethodIntercept6()))
                .make();

        DynamicType.Loaded<TestSuperClass> load = unloaded.load(getClass().getClassLoader());
        Class<? extends TestSuperClass> loaded = load.getLoaded();
        loaded.getMethod("testStaticMethod").invoke(loaded);
        load.saveIn(new File(path));
    }


}
