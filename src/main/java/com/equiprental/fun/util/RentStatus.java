package com.equiprental.fun.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RentStatus {

    RETURNED("returned");

    private final String status;
}