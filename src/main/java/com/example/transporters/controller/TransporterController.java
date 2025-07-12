package com.example.transporters.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.transporters.dto.AssignmentRequest;
import com.example.transporters.dto.AssignmentResponse;
import com.example.transporters.dto.InputRequest;
import com.example.transporters.dto.InputResponse;
import com.example.transporters.exception.BadRequestException;
import com.example.transporters.service.TransporterService;

@RestController
@RequestMapping("/api/v1/transporters")
public class TransporterController {

    @Autowired
    private TransporterService transporterService;

    @PostMapping("/input")
    public InputResponse processInput(
            @RequestBody InputRequest inputRequest) {
        // Logic to process the input request and generate a response

        InputResponse response = transporterService.processInput(inputRequest);

        if (response == null || response.getStatus() == null || response.getStatus().equals("error")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    response != null ? response.getMessage() : "Failed to process input");
        }

        // if (response.getStatus().equals("error")) {

        // }
        return response;
    }

    // @GetMapping("/persistence-check")
    // public InputRequest checkPersistence() {
    //     return transporterService.getInputRequest();
    // }

    @PostMapping("/assignment")
    public AssignmentResponse assignTransporters(
            @RequestBody AssignmentRequest assignmentRequest) {
        // Logic to assign transporters based on the request
        AssignmentResponse response = transporterService.assignTransporters(assignmentRequest.getMaxTransporters());
        if (response.getStatus().startsWith("error")) {
            throw new BadRequestException(
                    "Failed to assign transporters: " + response.getStatus());
        }

        return response;
    }
}
