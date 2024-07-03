package com.sparta.springauth.jwt;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;

/**
 * Util : 특정한 매개변수(파라미터)에 대한 작업을 수행하는 메서드들이 존재하는 클래스
 *        다른 객체에 의존하지 않고 하나의 모듈로서 동작하는 클래스
 */

@Component
public class JwtUtil {
  /**
   * JWT 데이터
   *   AUTHORIZATION_HEADER : Header Key 값 (Cookie Name Value)
   *   AUTHORIZATION_KEY : 사용자 권한 값의 Key (ADMIN/USER)
   *   BEARER_PREFIX : Token 식별자
   *   TOKEN_TIME : 토큰 만료 시간
   *   secretKey : Base64 Encode 한 SecretKey
   *   key : Decode 된 SecretKey 를 담는 객체
   *   logger : 로그 설정
   */
  public static final String AUTHORIZATION_HEADER = "Authorization";

  public static final String AUTHORIZATION_KEY = "auth";

  public static final String BEARER_PREFIX = "Bearer ";

  private final long TOKEN_TIME = 60 * 60 * 1000L;  // 60분

  @Value("${jwt.secret.key}")
  private String secretKey;

  private Key key;

  private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

  public static final Logger logger = LoggerFactory.getLogger("JWT 관련 로그");


  @PostConstruct  // 객체를 한 번만 생성할 때 사용
  public void init() {
    byte[] bytes = Base64.getDecoder().decode(secretKey);
    key = Keys.hmacShaKeyFor(bytes);
  }
}
