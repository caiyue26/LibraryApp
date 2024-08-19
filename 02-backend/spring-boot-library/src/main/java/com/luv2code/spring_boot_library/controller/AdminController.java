// reviewed on 2024.07.15

package com.luv2code.spring_boot_library.controller;

import com.luv2code.spring_boot_library.requestmodels.AddBookRequest;
import com.luv2code.spring_boot_library.service.AdminService;
import com.luv2code.spring_boot_library.utils.ExtractJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("https://localhost:3000")
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PutMapping("/secure/increase/book/quantity")
    public void increaseBookQuantity(@RequestHeader(value="Authorization") String token,
                                     @RequestParam Long bookId) throws Exception {
        List<String> admin = ExtractJWT.extractRolesFromJWT(token, "http://yuecai.com/roles");
        if (!admin.contains("Admin")) {
            throw new Exception("Administration page only.");
        }
        adminService.increaseBookQuantity(bookId);
    }

    @PutMapping("/secure/decrease/book/quantity")
    public void decreaseBookQuantity(@RequestHeader(value="Authorization") String token,
                                     @RequestParam Long bookId) throws Exception {
        List<String> admin = ExtractJWT.extractRolesFromJWT(token, "http://yuecai.com/roles");
        if (!admin.contains("Admin")) {
            throw new Exception("Administration page only.");
        }
        adminService.decreaseBookQuantity(bookId);
    }

    @PostMapping("/secure/add/book")
    public void postBook(@RequestHeader(value="Authorization") String token,
                         @RequestBody AddBookRequest addBookRequest) throws Exception {
        List<String> admin = ExtractJWT.extractRolesFromJWT(token, "http://yuecai.com/roles");

        // print admin
        // System.out.println("Roles extracted: " + admin);

        if (!admin.contains("Admin")) {
            throw new Exception("Administration page only.");
        }
        adminService.postBook(addBookRequest);
    }

    @DeleteMapping("/secure/delete/book")
    public void deleteBook(@RequestHeader(value="Authorization") String token,
                           @RequestParam Long bookId) throws Exception {
        List<String> admin = ExtractJWT.extractRolesFromJWT(token, "http://yuecai.com/roles");

        if (!admin.contains("Admin")) {
            throw new Exception("Administration page only.");
        }
        adminService.deleteBook(bookId);
    }
}
