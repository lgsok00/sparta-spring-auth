package com.sparta.springauth.controller;

import com.sparta.springauth.dto.SignupRequestDto;
import com.sparta.springauth.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/api")
public class UserController {

  private static final Logger log = LoggerFactory.getLogger(UserController.class);
  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/user/login-page")
  public String loginPage() {
    return "login";
  }

  @GetMapping("/user/signup")
  public String signupPage() {
    return "signup";
  }

  /**
   * 회원가입 API
   *
   * @param requestDto    회원가입 요청 데이터
   * @param bindingResult 오류 정보 객체
   * @return 로그인 페이지
   */
  @PostMapping("/user/signup")
  public String signup(@Valid SignupRequestDto requestDto, BindingResult bindingResult) {
    // Validation 예외 처리
    List<FieldError> fieldErrors = bindingResult.getFieldErrors();  // 발생한 오류들의 대한 정보가 담긴 리스트를 반환

    if (!fieldErrors.isEmpty()) {
      for (FieldError fieldError : bindingResult.getFieldErrors()) {
        log.error("{} 필드 : {}", fieldError.getField(), fieldError.getDefaultMessage());
      }

      return "redirect:/api/user/signup";
    }

    userService.signup(requestDto);

    return "redirect:/api/user/login-page";
  }
}
