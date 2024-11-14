package cn.crabapples.system.websocket.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
//@ServerEndpoint(value = "/ws/{id}", configurator = CustomSpringConfigure.class)
@ServerEndpoint(value = "/websocket/{id}")
public class WebSocketController {

    public static final ConcurrentHashMap<String, Session> WEB_SOCKET_CLIENT = new ConcurrentHashMap<>(16);

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("id") String id) {
        log.info("当前连接的id:[{}]", id);
        WEB_SOCKET_CLIENT.put(id, session);
        sendMessage(session, "连接成功" + id);
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session, @PathParam("id") String id) {
        WEB_SOCKET_CLIENT.remove(id);
        try {
            session.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 收到客户端消息后调用的方法
     */
    @OnMessage
    public void onMessage(Session session, String message, @PathParam("id") String sid) {
        log.info("收到消息id:[{}],内容:[{}]", sid, message);
        WEB_SOCKET_CLIENT.put(sid, session);
//        sendMessage(session, "收到来自" + sid + "的信息:" + message);
    }

    @OnError
    public void onError(Session session, Throwable error, @PathParam("id") String id) {
        WEB_SOCKET_CLIENT.remove(id);
        try {
            session.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 实现服务器主动推送
     */
    public void sendMessage(Session session, String message) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
