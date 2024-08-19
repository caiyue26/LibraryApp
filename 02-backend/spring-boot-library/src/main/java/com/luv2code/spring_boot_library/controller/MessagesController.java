// reviewed on 2024.07.14

package com.luv2code.spring_boot_library.controller;

import com.luv2code.spring_boot_library.entity.Message;
import com.luv2code.spring_boot_library.requestmodels.AdminQuestionRequest;
import com.luv2code.spring_boot_library.service.MessagesService;
import com.luv2code.spring_boot_library.utils.ExtractJWT;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@CrossOrigin("https://localhost:3000")
@RestController
@RequestMapping("/api/messages")
public class MessagesController {

    private MessagesService messagesService;

    public MessagesController(MessagesService messagesService) {
        this.messagesService = messagesService;
    }

    @PostMapping("/secure/add/message")
    public void postMessage(@RequestHeader(value = "Authorization") String token,
                            @RequestBody Message messageRequest) {
        String userEmail = ExtractJWT.payloadJWTExtraction(token, "https://claims.yuecai.com/email");
        messagesService.postMessage(messageRequest, userEmail);
    }


    @PutMapping("/secure/admin/message")
    public void putMessage(@RequestHeader(value="Authorization") String token,
                           @RequestBody AdminQuestionRequest adminQuestionRequest) throws Exception {
        String userEmail = ExtractJWT.payloadJWTExtraction(token, "https://claims.yuecai.com/email");
        List<String> admin = ExtractJWT.extractRolesFromJWT(token, "http://yuecai.com/roles");

        // print admin
        // System.out.println("Roles extracted: " + admin);

        if (admin == null || !admin.contains("Admin")) {
            throw new Exception("Administration page only.");
        }
        messagesService.putMessage(adminQuestionRequest, userEmail);
    }
}
