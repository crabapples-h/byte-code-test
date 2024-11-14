package cn.crabapples.system.websocket;

import cn.crabapples.common.websocket.AuthHandshakeInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.*;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.websocket.Session;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TODO WebSocket连接端点
 * 第二种方式:注册WebSocketHandler
 *
 * @author Ms.He
 * 2024-11-14 23:48
 * e-mail crabapples.cn@gmail.com
 * qq 294046317
 * pc-name mshe
 */
@Configuration
public class WebSocketServer2 implements WebSocketConfigurer {
    public static final ConcurrentHashMap<String, WebSocketSession> WEB_SOCKET_CLIENT = new ConcurrentHashMap<>(16);

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new WebSocketHandlerImpl(), "/ws/**")
                .addInterceptors(new AuthHandshakeInterceptor()) // 添加自定义拦截器
                .setAllowedOrigins("*"); // 允许的跨域来源
    }

    @Slf4j
    public static class WebSocketHandlerImpl implements WebSocketHandler {

        @Override
        public void afterConnectionEstablished(WebSocketSession session) throws Exception {
            String id = (String) session.getAttributes().get("id");
            log.info("websocket连接成功,连接方式[{}],客户端ID:[{}]", "配置", id);
            WEB_SOCKET_CLIENT.put(id, session);
        }

        @Override
        public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
            // 处理 WebSocket 消息
            String id = (String) session.getAttributes().get("id");
            SocketMessageSender.sendMessage2(message, id);
//            session.sendMessage(new TextMessage("Hello, " + session.getAttributes().get("user")));
        }

        @Override
        public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
            System.err.println("websocket连接异常,异常信息:" + exception);
        }

        @Override
        public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
            log.info("websocket连接关闭,关闭方式[{}],关闭原因:[{}]", closeStatus.getCode(), closeStatus.getReason());

        }

        @Override
        public boolean supportsPartialMessages() {
            System.err.println("不支持分片消息");
            return false;
        }

//        @Override
//        protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
//            // 处理 WebSocket 消息
//            SocketMessageSender.sendMessage2(format, id);
//
//            session.sendMessage(new TextMessage("Hello, " + session.getAttributes().get("user")));
//        }
    }
}