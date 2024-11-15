package cn.crabapples.common.jwt;

import cn.crabapples.common.base.ApplicationException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
//import io.jsonwebtoken.*;
import com.auth0.jwt.interfaces.RSAKeyProvider;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Collections;
import java.util.Date;

//import static io.jsonwebtoken.SignatureAlgorithm.HS256;

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
//        return parseToken(token).getSubject();
        return null;
    }

    public String getUserName() {
        String token = request.getHeader(authKey);
        return getUserName(token);
    }

    public String getUserName(String token) {
//        return (String) parseToken(token).get("username");
        return null;

    }

    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException {
//        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec("seed".getBytes());
//        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
//        PublicKey publicKey = keyFactory.generatePublic(keySpec);


        RSAPublicKey publicKey = (RSAPublicKey) createKey("seed").getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) createKey("seed").getPrivate();

        Algorithm algorithm = Algorithm.RSA256(null, privateKey);
        String jwt = JWT.create()
                .withIssuer("auth0")
                .withSubject("1234567890")
                .withClaim("role", "admin")
                .sign(algorithm);
//                .sign(Algorithm.HMAC256("aaa"));
        System.err.println(jwt);
        Algorithm verifyAlgorithm = Algorithm.RSA256(publicKey, null);

        DecodedJWT decodedJWT = JWT.require(verifyAlgorithm).build().verify(jwt);
//        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256("aaa")).build().verify(jwt);
        System.err.println(decodedJWT);
    }

    private static KeyPair createKey(String seed) throws NoSuchAlgorithmException {
//        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(seed.getBytes());
//        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//        Provider provider = keyFactory.getProvider();

        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(2048, new SecureRandom(seed.getBytes()));
        return keyPairGen.generateKeyPair();
    }

    public boolean parseToken(String token) {
        try {
            RSAPublicKey publicKey = (RSAPublicKey) createKey("seed").getPublic();
            RSAPrivateKey privateKey = (RSAPrivateKey) createKey("seed").getPrivate();
            JWT.require(Algorithm.RSA256(publicKey, privateKey)).build().verify(token);
            return true;
        } catch (Exception e) {
            log.warn("Token过期", e);
            return false;
        }
    }

    public String createToken(String subject, String username) {
        try {
            long nowMillis = System.currentTimeMillis();
            Date now = new Date();
            Date exp = new Date(nowMillis + expiresSecond);
            RSAPublicKey publicKey = (RSAPublicKey) createKey("seed").getPublic();
            RSAPrivateKey privateKey = (RSAPrivateKey) createKey("seed").getPrivate();
            String jwt = JWT.create()
                    .withHeader(Collections.singletonMap("type", "LOGIN"))
                    .withClaim("username", username)
                    .withSubject(subject)
                    .withIssuer("this is issuer")
                    .withIssuedAt(now)
                    .withExpiresAt(exp)
                    .withNotBefore(now)
                    .withAudience("jwtConfigure.getName()")
                    .sign(Algorithm.RSA256(publicKey, privateKey));
            JWT.create()
                    //                .setHeaderParam("type", "JWT")
//                .claim("username", username)
//                .signWith(HS256, createKey(base64Secret))
//                .compact();

                    .withHeader(Collections.singletonMap("type", "LOGIN"))
                    .withClaim("username", "this is username")
                    .withSubject("this is subject")// 这个JWT的主体，即它的所有人
                    .withIssuer("this is issuer")// 这个JWT的签发主体
//                .withIssuedAt(now) //JWT的签发时间
//                .withExpiresAt(now1) // JWT的过期时间
//                .withNotBefore(now) // JWT生效的开始时间
                    .withAudience("jwtConfigure.getName()")// 这个JWT的接收对象
                    .sign(Algorithm.RSA256(publicKey, privateKey));
//                .sign(Algorithm.HMAC256("aaa"));


            return JWT.create()
                    .withHeader(Collections.singletonMap("type", "JWT"))
                    .withClaim("username", username)
                    .withSubject(subject)
                    .withIssuer("jwtConfigure.getClientId()")
                    .withIssuedAt(now)
                    .withNotBefore(now)
                    .withAudience("jwtConfigure.getName()")
                    //                .sign(Algorithm.RSA256(null,null));
                    .sign(Algorithm.HMAC256("aaa"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
//                .setHeaderParam("type", "JWT")
//                .claim("username", username)
        // 这个JWT的主体，即它的所有人
//                .setSubject(subject)
        // 这个JWT的签发主体
//                .setIssuer("jwtConfigure.getClientId()")
        // 这个JWT的接收对象
//                .setAudience("jwtConfigure.getName()")
//                .signWith(HS256, createKey(base64Secret))
        // 是一个时间戳，这个JWT的签发时间；
//                .setIssuedAt(new Date())
        // 是一个时间戳，这个JWT生效的开始时间，意味着在这个时间之前验证JWT是会失败的
//                .setNotBefore(now)
        // 是一个时间戳，这个JWT的过期时间；
//                .setExpiration(exp)
//                .compact();
    }
}
