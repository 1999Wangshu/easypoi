package com.example.easypoi.pojo;


import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.TestInstance;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserTest implements Serializable {

    private Integer id;
    private String username;
    private BigDecimal password;
    private BigDecimal age;
    private Integer height;

}
