package com.sherry.ecom.category;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CategoryRequest {
    private String name;
    private Integer level;
    private Integer parentId;
}
