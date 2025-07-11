package com.example.email_writer.service;

import java.lang.runtime.ObjectMethods;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.email_writer.EmailRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@Service
public class EmailGeneratorService {
    private final WebClient webClient;
    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    public EmailGeneratorService(WebClient.Builder webClientBuilder){
        this.webClient=webClientBuilder.build();
    }
    
    public String generateEmailReply(EmailRequest emailRequest){

        // build the prompt
        String prompt=buildPrompt(emailRequest);
        // craft a request in the below format
        /*
                {
                    "contents": [
                    {
                        "parts": [
                        {
                            "text": "Explain how AI works in a few words"
                        }
                        ]
                    }
                    ]
                }
            */
            // we use map since the request is of key value pairs
            Map<String,Object> requestBody=Map.of(
                "contents",new Object[]{
                    Map.of(
                        "parts",new Object[]{
                            Map.of("text",prompt)
                        }
                    )
                }
            );
            // do request and get response
            String response=webClient.post()
                            .uri(geminiApiUrl + geminiApiKey)
                            .header("Content-Type","application/json")
                            .bodyValue(requestBody)
                            .retrieve().bodyToMono(String.class)
                            .block();  
            // here response means
            /*
            {
        "candidates": [
            {
                "content": {
                    "parts": [
                        {
                            "text": "AI learns from data to make predictions or decisions.\n"
                        }
                    ],
                    "role": "model"
                },
                "finishReason": "STOP",
                "avgLogprobs": -0.076992181214419281
            }
        ],
        "usageMetadata": {
            "promptTokenCount": 8,
            "candidatesTokenCount": 11,
            "totalTokenCount": 19,
            "promptTokensDetails": [
                {
                    "modality": "TEXT",
                    "tokenCount": 8
                }
            ],
            "candidatesTokensDetails": [
                {
                    "modality": "TEXT",
                    "tokenCount": 11
                }
            ]
        },
        "modelVersion": "gemini-2.0-flash",
        "responseId": "94hwaJ__AqP01PIPtf2LqAw"
    }
         */
        // from this we need only "text" field so we extract onlt text field
        // extract response and return response
        return extractResponseContent(response);
    }

    private String extractResponseContent(String response) {
       try{
            ObjectMapper mapper=new ObjectMapper();
            JsonNode rootNode=mapper.readTree(response);
            return rootNode.path("candidates")
                            .get(0).
                            path("content").
                            path("parts").
                            get(0).
                            path("text")
                            .asText();
       }
       catch(Exception e){
        return "Error processing request: "+e.getMessage();
       }
    }

    private String buildPrompt(EmailRequest emailRequest) {
       StringBuilder prompt=new StringBuilder();
       prompt.append("Generate a professional email reply for the following email content. Please don't generate a subject line. ");
       if(emailRequest.getTone()!=null&&emailRequest.getTone().isEmpty()){
        prompt.append("Use a ").append(emailRequest.getTone()).append(" tone.");
       }
       prompt.append("\nOriginal email: \n").append(emailRequest.getEmailContent());
       return prompt.toString();
    }

}
