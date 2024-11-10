package base;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestSuperClass {
    public TestSuperClass() {
        log.info("执行了父类构造方法:[{}]", "TestSuperClass()");
    }

    public String selectUserName(String id) {
        log.info("执行了父类[{}]方法，参数:[{}]", "selectUserName()",id);
        return String.format("用户Id:[%s]", id);
    }

    public int selectAge(Integer age) {
        log.info("执行了父类[{}]方法，参数:[{}]", "selectAge()",age);
        return age;
    }

    public String selectGander(Integer gender) {
        log.info("执行了父类[{}]方法，参数:[{}]", "selectGander()",gender);
        return String.format("用户性别:[%d]", gender);
    }

    public Object testMethod() {
        log.info("执行了父类[{}]方法", "testMethod()");
        return "source Method";
    }

    public Object testMethod1(int number) {
        log.info("执行了父类[{}]方法，参数:[{}]", "testMethod1()",number);
        return "source Method1:" + number;
    }

    public static void testStaticMethod() {
        log.info("执行了父类静态方法:[{}]", "testStaticMethod()");
    }

}
