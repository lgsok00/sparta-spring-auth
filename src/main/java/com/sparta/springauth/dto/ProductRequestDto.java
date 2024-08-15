package com.sparta.springauth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Negative;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ProductRequestDto {

  @NotBlank
  private String name;
  @Email
  private String email;
  @Positive(message = "양수만 가능합니다.")
  private int price;
  @Negative(message = "음수만 가능합니다.")
  private int discount;
  @Size(min = 2, max = 10)
  private String link;
  @Max(10)
  private int max;
  @Min(2)
  private int min;
}
