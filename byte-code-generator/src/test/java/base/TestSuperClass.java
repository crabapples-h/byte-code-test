package base;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestSuperClass {
    public TestSuperClass() {
        String format = String.format("执行了父类构造方法:[%s]", "TestSuperClass()");
        System.err.println(format);
    }

    public String selectUserName(String id) {
        String format = String.format("执行了父类[%s]方法，参数:[%s]", "selectUserName()", id);
        System.err.println(format);
        return String.format("用户Id:[%s]", id);
    }

    public int selectAge(Integer age) {
        String format = String.format("执行了父类[%s]方法，参数:[%s]", "selectAge()", age);
        System.err.println(format);
        return age;
    }

    public String selectGander(Integer gender) {
        String format = String.format("执行了父类[%s]方法，参数:[%s]", "testMethod()", gender);
        System.err.println(format);
        return String.format("用户性别:[%d]", gender);
    }

    public Object testMethod() {
        String format = String.format("执行了父类[%s]方法", "testMethod()");
        System.err.println(format);
        return format;
    }

    public Integer testMethod1(int number) {
        String format = String.format("执行了父类[%s]方法，参数:[%s]", "testMethod1()", number);
        System.err.println(format);
        return number;
    }

    public static void testStaticMethod() {
        log.info("执行了父类静态方法:[{}]", "testStaticMethod()");
    }

}
