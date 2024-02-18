package com.wepos.authentication.dto;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class Page {
    private int total;
    private int current;
    private int rowCount;

}
