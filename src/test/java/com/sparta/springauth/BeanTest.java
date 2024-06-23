package com.sparta.springauth;

import com.sparta.springauth.food.Food;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class BeanTest {

  @Autowired
  @Qualifier("pizza")
  Food food;  // 자동 주입 불가 -> Food 타입의 Bean 객체가 한 개 이상
//  Food chicken;  // 빈 객체 직접 주입

//  @Autowired
//  Food pizza;

  @Test
  @DisplayName("Primary 와 Qualifier 우선순위 확인")
  void test1() {
    food.eat();
  }
}
