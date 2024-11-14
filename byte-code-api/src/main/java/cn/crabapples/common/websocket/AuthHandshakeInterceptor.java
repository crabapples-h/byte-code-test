package cn.crabapples.common.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * TODO 使用配置方式的拦截器
 *
 * @author Mr.He
 * 2024-11-14 23:51
 * e-mail crabapples.cn@gmail.com
 * qq 294046317
 * pc-name mshe
 */
@Slf4j
public class AuthHandshakeInterceptor implements HandshakeInterceptor {
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        HttpHeaders headers = request.getHeaders();
        List<String> tokenList = headers.get("sec-websocket-protocol");
        headers.remove("sec-websocket-protocol");
        if (Objects.nonNull(tokenList) && !tokenList.isEmpty()) {
            String token = tokenList.get(0);
            // 检查 Token 是否存在，并进行验证
            if (validateToken(token)) {
                // Token 验证通过，可以将用户信息添加到 WebSocket session attributes 中
                attributes.put("user", getUserFromToken(token));
                response.getHeaders().put("sec-websocket-protocol", Collections.singletonList(token));
                // 允许握手
                return true;
            } else {
                // Token 验证失败，拒绝握手
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return true;
//                return false;
            }
        }
        return true;
//        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler
            wsHandler, Exception exception) {
        // 握手完成后的处理逻辑（可以留空）
    }

    // 验证 Token 的方法
    private boolean validateToken(String token) {
        if (token != null) {
            // 在这里实现 Token 验证逻辑，例如调用认证服务或者解析 JWT
            return true; // 示例：直接比较字符串（实际应用中应使用更复杂的逻辑）
        }
        return false;
    }

    // 从 Token 中获取用户信息的方法
    private String getUserFromToken(String token) {
        // 解析 Token，提取用户信息
        return "username"; // 示例：直接返回用户名（实际应用中应根据 Token 获取用户信息）
    }

}
