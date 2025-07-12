package com.example.email_writer.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.email_writer.EmailRequest;
import com.example.email_writer.service.EmailGeneratorService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/email")
@AllArgsConstructor
@CrossOrigin(origins="*") // because frontend is from http://localhost:5173/ and back is 8080 port to in order to allow communicate between two different origin we use cross origin and * indicates allow communication for all types of ports and origins 
public class EmailGeneratorController {
    private final EmailGeneratorService emailGeneratorService;

    @PostMapping("/generate")
    public ResponseEntity<String> generateEmail(@RequestBody EmailRequest emailRequest){
        String response=emailGeneratorService.generateEmailReply(emailRequest);
        return ResponseEntity.ok(response);
    }

}
