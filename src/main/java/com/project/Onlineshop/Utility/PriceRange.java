package com.project.Onlineshop.Utility;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class PriceRange {
    private BigDecimal min;
    private BigDecimal max;

    public static PriceRange getMinMaxNumber(BigDecimal p1, BigDecimal p2) {
        BigDecimal min = p1;
        BigDecimal max = p2;
        if (min.compareTo(max) > 0) {
            min = p2;
            max = p1;
        }
        return new PriceRange(min, max);
    }

}

