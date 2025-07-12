package com.example.transporters.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransporterEntity {
    private int id;
    private String name;
    private List<LaneQuote> laneQuotes;
}
