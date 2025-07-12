package com.example.transporters.dto;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.transporters.model.LaneEntity;
import com.example.transporters.model.TransporterEntity;

import lombok.Getter;

@Getter
@Component
public class InputRequest {
    private List<LaneEntity> lanes;
    private List<TransporterEntity> transporters;
}
