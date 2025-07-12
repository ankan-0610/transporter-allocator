package com.example.transporters.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.transporters.dto.AssignmentResponse;
import com.example.transporters.dto.InputRequest;
import com.example.transporters.dto.InputResponse;
import com.example.transporters.model.Assignment;
import com.example.transporters.model.LaneEntity;
import com.example.transporters.model.LaneQuote;
import com.example.transporters.model.SelectAssignment;
import com.example.transporters.model.TransporterEntity;
import com.example.transporters.model.TransporterQuote;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TransporterService {
    // This service will handle the business logic for transporters and lanes.

    @Autowired
    private ObjectMapper objectMapper;
    private Map<Integer, List<TransporterQuote>> laneToTransporterMap = new HashMap<>();
    private List<Integer> transporterIds = new ArrayList<>();
    private List<Integer> laneIds = new ArrayList<>();
    private Map<Integer, Boolean> laneCoverage = new HashMap<>();

    // Example method to process input request
    public InputResponse processInput(InputRequest request) {

        // Validate input
        if (request.getLanes() == null || request.getTransporters() == null) {
            return new InputResponse("error", "Invalid input data");
        }

        mapLaneToTransporters(request.getTransporters());
        setTransporterIds(request.getTransporters());
        setLaneIds(request.getLanes());

        return new InputResponse("success", "Input processed successfully");
    }

    private void setTransporterIds(List<TransporterEntity> transporters) {
        for (TransporterEntity transporter : transporters) {
            transporterIds.add(transporter.getId());
        }
    }
    
    private void setLaneIds(List<LaneEntity> lanes) {
        for (LaneEntity lane : lanes) {
            laneIds.add(lane.getId());
        }
    }
    
    private void mapLaneToTransporters(List<TransporterEntity> transporters) {
        laneToTransporterMap = new HashMap<>();

        for (TransporterEntity transporter : transporters) {
            for (LaneQuote laneQuote : transporter.getLaneQuotes()) {
                TransporterQuote transporterQuote = new TransporterQuote(transporter.getId(), laneQuote.getQuote());

                laneToTransporterMap
                    .computeIfAbsent(laneQuote.getLaneId(), k -> new ArrayList<>())
                    .add(transporterQuote);
            }
        }
    }
    
    private SelectAssignment assignSelectedTransporters(List<Integer> transporterSet) {
        Map<Integer, Integer> freqTransporterId = new HashMap<>();
        List<Assignment> assignments = new ArrayList<>();
        TreeSet<Integer> finalTransporters = new TreeSet<>();
        
        long[] totalQuote = { 0 };

        // allocate transporters optimally
        laneToTransporterMap.forEach((laneId, transporterQuotes) -> {
            laneCoverage.put(laneId, true);
            int minQuote = Integer.MAX_VALUE;
            int minTransporterId = -1;
            List<Integer> selectedTransporters = new ArrayList<>();

            for (TransporterQuote transporterQuote : transporterQuotes) {
                if (transporterSet.contains(transporterQuote.getTransporterId()) &&
                        transporterQuote.getQuote() <= minQuote) {

                    minQuote = transporterQuote.getQuote();
                    selectedTransporters.add(transporterQuote.getTransporterId());
                }
            }
            
            // try to select least used transporter
            int minFreq = transporterQuotes.size();
            for (int id : selectedTransporters) {
                if (freqTransporterId.getOrDefault(freqTransporterId, 0) < minFreq) {
                    minFreq = freqTransporterId.getOrDefault(id, 0);
                    minTransporterId = id;
                }
            }

            totalQuote[0] += minQuote;
            freqTransporterId.put(minTransporterId, freqTransporterId.getOrDefault(minTransporterId, 0) + 1);
            assignments.add(new Assignment(laneId, minTransporterId));
            finalTransporters.add(minTransporterId);
        });

        // convert TreeSet to List
        List<Integer> finalTransportersList = new ArrayList<>(finalTransporters);

        return new SelectAssignment(totalQuote[0], assignments, finalTransportersList);
    }
    
    public AssignmentResponse assignTransporters(int maxTransporters) {
        if(transporterIds.size()==0 || laneIds.size()==0) {
            return new AssignmentResponse("error: no transporters or lanes available", -1, new ArrayList<>(), new ArrayList<>());
        }

        if (maxTransporters == 0) {
            return new AssignmentResponse("error: insufficient value of maxTransporters", -1, new ArrayList<>(),
                    new ArrayList<>());
        }

        // Generate all combinations of transporterIds with size maxTransporters
        List<List<Integer>> transporterCombinations = CombinationService.generateSortedCombinations(transporterIds,
                maxTransporters);

        // minimize total Quote
        long minTotalQuote = Long.MAX_VALUE;
        List<Assignment> assignments = new ArrayList<>();
        List<Integer> selectedTransporters = new ArrayList<>();
        boolean foundAssignment = false;

        // check total Quote for all the combinations
        for (List<Integer> transporterSet : transporterCombinations) {
            SelectAssignment selectAssignment = assignSelectedTransporters(transporterSet);
            // try {
            //     log.info("for set: " + transporterSet);
            //     log.info("selectAssignment: {}", objectMapper.writeValueAsString(selectAssignment));
            // } catch (JsonProcessingException e) {
            //     log.warn("Failed to serialize object", e);
            // }

            if (!hasBadAssignments(selectAssignment.getAssignments()) &&
                    selectAssignment.getTotalQuote() < minTotalQuote) {

                foundAssignment = true;
                minTotalQuote = selectAssignment.getTotalQuote();
                assignments = selectAssignment.getAssignments();
                selectedTransporters = selectAssignment.getSelectedTransporters();
            }
        }

        if (foundAssignment && checkLaneCoverage()) {
            return new AssignmentResponse("success", minTotalQuote, assignments, selectedTransporters);
        }
        return new AssignmentResponse("error: insufficient transporters to cover lanes", -1, new ArrayList<>(), new ArrayList<>());
    }
    
    private boolean hasBadAssignments(List<Assignment> assignments) {
        for (Assignment assignment : assignments) {
            if (assignment.getTransporterId() == -1) {
                return true;
            }
        }
        return false;
    }

    private boolean checkLaneCoverage() {
        for (Map.Entry<Integer, Boolean> entry : laneCoverage.entrySet()) {
            if (!entry.getValue()) {
                return false;
            }
        }
        return true;
    }
}
