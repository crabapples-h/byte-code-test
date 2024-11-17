package cn.crabapples;

import cn.crabapples.turing.TuringApiService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@SpringBootTest(classes = {SpringBootApplicationTest.class})

@RunWith(SpringRunner.class)
@ActiveProfiles("dev")
//@WebAppConfiguration
public class SpringBootApplicationTest {
    @Autowired
    TuringApiService apiService;

    @Test
    public void case1() {
        System.out.println(apiService);
        String s = apiService.sendMessage("你知道chatgpt吗","001",0);
        System.err.println(s);
    }
}
