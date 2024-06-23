package com.sparta.springauth.food;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary  // 같은 타입의 Bean 객체가 여러 개 일 때 우선적으로 주입됨 (범용적)
public class Chicken implements Food {

  @Override
  public void eat() {
    System.out.println("치킨을 먹습니다.");
  }
}
