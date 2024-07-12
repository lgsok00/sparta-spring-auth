package com.sparta.springauth.auth;

import com.sparta.springauth.entity.UserRoleEnum;
import com.sparta.springauth.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@RestController
@RequestMapping("/api")
public class AuthController {

  public static final String AUTHORIZATION_HEADER = "Authorization";

  private final JwtUtil jwtUtil;

  public AuthController(JwtUtil jwtUtil) {
    this.jwtUtil = jwtUtil;
  }

  /**
   * Cookie 생성 메서드
   * @param response
   * @return
   */
  @GetMapping("/create-cookie")
  public String createCookie(HttpServletResponse response) {
    addCookie("Robbie Auth", response);

    return "createCookie";
  }

  /**
   * Cookie 조회 메서드
   * @param value
   * @return
   */
  @GetMapping("/get-cookie")
  public String getCookie(@CookieValue(AUTHORIZATION_HEADER) String value) {
    System.out.println("Cookie Value: " + value);

    return "getCookie : " + value;
  }

  /**
   * 세션 생성 메서드
   * @param request
   * @return
   */
  @GetMapping("/create-session")
  public String createSession(HttpServletRequest request) {
    // 세션이 존재할 경우 세션 반환, 없을 경우 새로운 세션을 생성 후 반환
    HttpSession session = request.getSession(true);

    // 세션에 저장될 정보 Name - Value 를 추가합니다.
    session.setAttribute(AUTHORIZATION_HEADER, "Robbie Auth");

    return "createSession";
  }

  /**
   * 세션 조회 메서드
   * @param request
   * @return
   */
  @GetMapping("/get-session")
  public String getSession(HttpServletRequest request) {
    // 세션이 존재할 경우 세션 반환, 없을 경우 null 반환
    HttpSession session = request.getSession(false);

    String value = (String) session.getAttribute(AUTHORIZATION_HEADER);  // 가져온 세션에 저장된 Value 를 Name 을 사용하여 가져온다.
    System.out.println("value = " + value);

    return "getSession : " + value;
  }

  /**
   * JWT 생성 메서드
   * @param response  Response 객체
   * @return  JWT 토큰
   */
  @GetMapping("/create-jwt")
  public String createJWT(HttpServletResponse response) {
    // JWT 생성
    String token = jwtUtil.createToken("Robbie", UserRoleEnum.USER);

    // JWT 를 쿠키에 저장
    jwtUtil.addJwtToCookie(token, response);

    return "createJWT : " + token;
  }

  /**
   * JWT 조회 메서드
   * @param tokenValue  쿠키에 저장된 JWT 토큰
   * @return  Bearer 가 제거된 JWT 토큰
   */
  @GetMapping("/get-jwt")
  public String getJWT(@CookieValue(JwtUtil.AUTHORIZATION_HEADER) String tokenValue) {
    // JWT substring
    String token = jwtUtil.substringToken(tokenValue);

    // JWT 검증
    if (!jwtUtil.validateToken(token)) {
      throw new IllegalArgumentException("Token Error");
    }

    // JWT 에서 사용자 정보 추출
    Claims info = jwtUtil.getUserInfoFromToken(token);
    // 사용자 이름
    String username = info.getSubject();
    System.out.println("username = " + username);

    // 사용자 권한
    String authority = (String) info.get(JwtUtil.AUTHORIZATION_KEY);
    System.out.println("authority = " + authority);

    return "getJWT : " + username + ", " + authority;
  }


  /**
   * Cookie 저장 메서드
   * @param cookieValue
   * @param response
   */
  public static void addCookie(String cookieValue, HttpServletResponse response) {
    try {
      cookieValue = URLEncoder.encode(cookieValue, "utf-8").replaceAll("\\+", "%20");  // Cookie Value 에는 공백이 불가능하므로 encoding 진행

      Cookie cookie = new Cookie(AUTHORIZATION_HEADER, cookieValue);  // Name-Value
      cookie.setPath("/");
      cookie.setMaxAge(30 * 60);

      // Response 객체에 Cookie 추가
      response.addCookie(cookie);

    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e.getMessage());
    }
  }
}
