package com.example.transporters.dto;

import java.util.List;

import com.example.transporters.model.Assignment;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AssignmentResponse {
    private String status;
    private long totalCost;
    private List<Assignment> assignments;
    private List<Integer> selectedTransporters;
}
