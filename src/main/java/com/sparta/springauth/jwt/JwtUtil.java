package com.sparta.springauth.jwt;

import com.sparta.springauth.entity.UserRoleEnum;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

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

  /**
   * JWT 생성 메서드
   * @param username  사용자 이름
   * @param role  사용자 권한
   * @return  JWT 토큰
   */
  public String createToken(String username, UserRoleEnum role) {
    Date date = new Date();

    return BEARER_PREFIX +
            Jwts.builder()
                    .setSubject(username)  // 사용자 식별자 값(ID)
                    .claim(AUTHORIZATION_KEY, role)  // 사용자 권한
                    .setExpiration(new Date(date.getTime() + TOKEN_TIME))  // 토큰 만료 시간
                    .setIssuedAt(date)  // 발급일
                    .signWith(key, signatureAlgorithm)  // 암호화 알고리즘
                    .compact();
  }

  /**
   * JWT 를 Cookie 에 저장하는 메서드
   * @param token  JWT 토큰
   * @param response  Response 객체
   */
  public void addJwtToCookie(String token, HttpServletResponse response) {
    try {
      token = URLEncoder.encode(token, "utf-8").replaceAll("\\+", "%20");  // Cookie Value 에는 공백이 불가능

      Cookie cookie = new Cookie(AUTHORIZATION_HEADER, token);  // Name-Value
      cookie.setPath("/");

      // Response 객체에 Cookie 추가
      response.addCookie(cookie);

    } catch (UnsupportedEncodingException e) {
      logger.error(e.getMessage());
    }
  }

  /**
   * JWT 식별자를 substring 하는 메서드
   * @param tokenValue  BEARER_PREFIX + JWT
   * @return  JWT 토큰
   */
  public String substringToken(String tokenValue) {
    // hasText : 공백과 null 체크
    if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
      return tokenValue.substring(7);
    }

    logger.error("Not Found Token");
    throw new NullPointerException("Not Found Token");
  }

  /**
   * JWT 검증 메서드
   * @param token  JWT 토큰
   * @return  true or false
   */
  public boolean validateToken(String token) {
    try {
      // JWT 파싱
      Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
      return true;

    } catch (SecurityException | MalformedJwtException | SignatureException e) {
      logger.error("Invalid JWT signature, 유효하지 않은 JWT 서명 입니다.");
    } catch (ExpiredJwtException e) {
      logger.error("Expired JWT token, 만료된 JWT token 입니다.");
    } catch (UnsupportedJwtException e) {
      logger.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
    } catch (IllegalArgumentException e) {
      logger.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
    }
    return false;
  }

  /**
   * JWT 에서 사용자 정보 반환 메서드
   * @param token  JWT 토큰
   * @return  사용자 정보 Claim
   */
  public Claims getUserInfoFromToken(String token) {
    return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
  }

  /**
   * HttpServletRequest 객체에서 JWT 추출 메서드
   * @param request  HttpServletRequest 객체
   * @return  JWT
   */
  public String getTokenFromRequest(HttpServletRequest request) {
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (cookie.getName().equals(AUTHORIZATION_HEADER)) {
          try {
            return URLDecoder.decode(cookie.getValue(), "UTF-8");  // Encode 되어 넘어간 Value 다시 Decode
          } catch (UnsupportedEncodingException e) {
            return null;
          }
        }
      }
    }
    return null;
  }
}
