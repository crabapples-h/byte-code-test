package cn.crabapples.system.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.websocket.Session;
import java.io.IOException;

/**
 * TODO WebSocketService
 *
 * @author Mr.He
 * 2019/8/5 22:53
 * e-mail crabapples.cn@gmail.com
 * qq 294046317
 * pc-name 29404
 */
@Slf4j
public class SocketMessageSender {
    public static void sendMessage(String message, String id) throws IOException {
        Session session = WebSocketServer.WEB_SOCKET_CLIENT.get(id);
        if (session == null) {
            return;
        }
        session.getBasicRemote().sendText(message);
    }

    public static void sendMessage2(WebSocketMessage<?> message, String id) throws IOException {
        WebSocketSession session = WebSocketServer2.WEB_SOCKET_CLIENT.get(id);
        if (session == null) {
            return;
        }
        session.sendMessage(message);
    }
}
