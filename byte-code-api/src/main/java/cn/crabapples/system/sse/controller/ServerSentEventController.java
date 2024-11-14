package cn.crabapples.system.sse.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

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
//    @JwtIgnore
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
        return sseEmitter;
    }

    @RequestMapping("/send/{id}")
    public void sseTestSend(@PathVariable String id) throws IOException, InterruptedException {
        SseEmitter sseEmitter = SSE_CLENT_MAP.get(id);
        log.info("sse测试发送消息,id:[{}],[{}]", id, sseEmitter);
        for (int i = 0; i < 10; i++) {
            SseEmitter.SseEventBuilder sseEventBuilder = SseEmitter.event();
            TimeUnit.SECONDS.sleep(1);
            sseEventBuilder
                    .name("log")
                    .data(String.format("第[%d]次消息推送", i));
            sseEmitter.send(sseEventBuilder);
        }
    }
}
