package com.sparta.springauth.food;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("pizza")  // 같은 타입의 Bean 객체가 여러 개 일 때 우선적으로 주입됨 (지역적)
public class Pizza implements Food{

  @Override
  public void eat() {
    System.out.println("피자를 먹습니다.");
  }
}
