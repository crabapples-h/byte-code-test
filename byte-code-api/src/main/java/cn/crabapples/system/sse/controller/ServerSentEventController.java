package cn.crabapples.system.sse.controller;

import cn.crabapples.common.ResponseDTO;
import cn.crabapples.common.dic.EnableDict;
import cn.crabapples.common.jwt.JwtIgnore;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * TODO SSE接口(这个接口在node环境下会有问题，暂时只能在纯浏览器环境打开)
 *
 * @author Mr.He
 * 2021/6/16 9:14
 * e-mail crabapples.cn@gmail.com
 * qq 294046317
 * pc-name admin
 */
@RestController
@RequestMapping("/api/sse")
@Slf4j
public class ServerSentEventController {
    private static final Map<String, SseEmitter> SSE_CLENT_MAP = new ConcurrentHashMap<>();

    @RequestMapping(value = "/connect/{id}")
    @JwtIgnore
    public SseEmitter connectSse(@PathVariable String id) {
        log.info("收到sse请求,id:[{}]", id);
        SseEmitter sseEmitter = SSE_CLENT_MAP.get(id);
        if (Objects.isNull(sseEmitter)) {
            sseEmitter = new SseEmitter(1000 * 60 * 30L);
            sseEmitter.onTimeout(() -> SSE_CLENT_MAP.remove(id));
            sseEmitter.onError((e) -> SSE_CLENT_MAP.remove(id));
            sseEmitter.onCompletion(() -> SSE_CLENT_MAP.remove(id));
            SSE_CLENT_MAP.put(id, sseEmitter);
        }
        SseEmitter finalSseEmitter = sseEmitter;
        new Thread(() -> {
            try {

                for (int i = 0; i < 20; i++) {
                    TimeUnit.SECONDS.sleep(1);
                    JSONObject object = new JSONObject();
                    object.put("data", String.format("第[%d]次消息推送", i));
                    SseEmitter.SseEventBuilder sseEventBuilder = SseEmitter.event();
                    SseEmitter.SseEventBuilder data = sseEventBuilder
                            .name("log")
                            .data(object);
                    finalSseEmitter.send(data);
//                    finalSseEmitter.send(String.format("第[%d]次消息推送", i));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        return sseEmitter;
    }

    @RequestMapping("/send")
    public void sseTestSend(String id) throws IOException {
        SseEmitter sseEmitter = SSE_CLENT_MAP.get(id);
        log.info("sse测试发送消息,id:[{}],[{}]", id, sseEmitter);
        System.err.println(sseEmitter);
        for (int i = 0; i < 20; i++) {
            SseEmitter.SseEventBuilder sseEventBuilder = SseEmitter.event();
            sseEventBuilder
                    .name("log")
                    .data(String.format("第[%d]次消息推送", i));
            sseEmitter.send(sseEventBuilder);
//        sseEmitter.send(String.format("第[%d]次消息推送", 1));
        }
        return;
    }
}
