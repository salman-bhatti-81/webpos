package com.wepos.authentication.dto;

import com.wepos.domain.Item;
import lombok.Data;

import java.util.List;

@Data
public class ItemsResponse {
    private List<Item> items;
    private Page page;
}




