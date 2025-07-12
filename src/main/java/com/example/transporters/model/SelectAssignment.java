package com.example.transporters.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SelectAssignment {
    private long totalQuote;
    private List<Assignment> assignments;
    private List<Integer> selectedTransporters;
}
