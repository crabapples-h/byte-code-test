package cn.crabapples.common.jwt;

import cn.crabapples.common.base.ApplicationException;
import io.jsonwebtoken.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;

/**
 * TODO jwt工具(用于解析和生成token)
 *
 * @author Mr.He
 * 9/5/20 2:52 PM
 * e-mail crabapples.cn@gmail.com
 * qq 294046317
 * pc-name root
 */
@Slf4j
@Getter
@Setter
@Component
@PropertySource(value = {"classpath:application-custom.properties"})
@ConfigurationProperties(prefix = "crabapples.jwt")
public class JwtTokenUtils {

    private String authKey; // 授权信息在header中的key
    private String clientId;
    private String base64Secret;
    private String name;
    private int expiresSecond;

    private final HttpServletRequest request;

    public JwtTokenUtils(HttpServletRequest request) {
        this.request = request;
    }
    public String getUserId() {
        String token = request.getHeader(authKey);
        return getUserId(token);
    }

    public String getUserId(String token) {
        return parseToken(token).getSubject();
    }

    public String getUserName() {
        String token = request.getHeader(authKey);
        return getUserName(token);
    }

    public String getUserName(String token) {
        return (String) parseToken(token).get("username");
    }

    public Claims parseToken(String token) {
        try {
            byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(base64Secret);
            // 使用HS256加密算法
            SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
            Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
            JwtParser jwtParser = Jwts.parser().setSigningKey(signingKey);
            Jws<Claims> claimsJws = jwtParser.parseClaimsJws(token);
            Claims body = claimsJws.getBody();
            return body;
        } catch (ExpiredJwtException e) {
            log.warn("Token过期", e);
            throw new ApplicationException("登录状态异常", 401);
        } catch (Exception e) {
            log.warn("token解析异常", e);
            throw new ApplicationException("登录状态异常", 401);
        }
    }

    public String createToken(String subject, String username) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Date exp = new Date(nowMillis + expiresSecond);
        // 使用HS256加密算法
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        //生成签名密钥
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(base64Secret);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
        return Jwts.builder()
                .setHeaderParam("type", "JWT")
                .claim("username", username)
                // 这个JWT的主体，即它的所有人
                .setSubject(subject)
                // 这个JWT的签发主体
                .setIssuer("jwtConfigure.getClientId()")
                // 这个JWT的接收对象
                .setAudience("jwtConfigure.getName()")
                .signWith(signatureAlgorithm, signingKey)
                // 是一个时间戳，这个JWT的签发时间；
                .setIssuedAt(new Date())
                // 是一个时间戳，这个JWT生效的开始时间，意味着在这个时间之前验证JWT是会失败的
                .setNotBefore(now)
                // 是一个时间戳，这个JWT的过期时间；
                .setExpiration(exp)
                .compact();
    }
}
