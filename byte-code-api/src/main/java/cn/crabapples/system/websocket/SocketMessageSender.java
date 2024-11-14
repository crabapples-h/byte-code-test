package cn.crabapples.system.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.websocket.Session;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TODO WebSocket消息发送服务
 *
 * @author Mr.He
 * 2019/8/5 22:53
 * e-mail crabapples.cn@gmail.com
 * qq 294046317
 * pc-name 29404
 */
@Slf4j
public class SocketMessageSender {
    public static void sendMessageAllSimple(String message, String id) throws IOException {
        ConcurrentHashMap.KeySetView<String, Session> keys = WebSocketServerSimple.WEB_SOCKET_CLIENT.keySet();
        for (String key : keys) {
            Session session = WebSocketServerSimple.WEB_SOCKET_CLIENT.get(key);
            session.getBasicRemote().sendText(message);
        }
    }

    public static void sendMessageSimple(String message, String id) throws IOException {
        Session session = WebSocketServerSimple.WEB_SOCKET_CLIENT.get(id);
        if (session == null) {
            return;
        }
        session.getBasicRemote().sendText(message);
    }

    public static void sendMessageAllAdvanced(WebSocketMessage<?> message, String id) throws IOException {
        ConcurrentHashMap.KeySetView<String, WebSocketSession> keys = WebSocketServerAdvanced.WEB_SOCKET_CLIENT.keySet();
        for (String key : keys) {
            WebSocketSession session = WebSocketServerAdvanced.WEB_SOCKET_CLIENT.get(key);
            session.sendMessage(message);
        }
    }

    public static void sendMessageAdvanced(WebSocketMessage<?> message, String id) throws IOException {
        WebSocketSession session = WebSocketServerAdvanced.WEB_SOCKET_CLIENT.get(id);
        if (session == null) {
            return;
        }
        session.sendMessage(message);
    }
}
