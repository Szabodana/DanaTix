package com.project.danatix.controllers;

import com.project.danatix.models.DTOs.NewUserDTO;
import com.project.danatix.models.DTOs.UserDataReceivedDTO;
import com.project.danatix.models.User;
import com.project.danatix.services.AuthenticationServiceImpl;
import com.project.danatix.services.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("api/users")
public class ProfileController {


    private final UserServiceImpl userService;

    @Autowired
    public ProfileController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @PatchMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> handlePatchRequest(@RequestBody Map<String, String> requestBody, @AuthenticationPrincipal User user) {

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        List<String> errorList = new ArrayList<>();

        if (requestBody.containsKey("name") ||
                requestBody.containsKey("email") ||
                requestBody.containsKey("password")&&requestBody.containsKey("currentPassword")) {

            UserDataReceivedDTO userData = new UserDataReceivedDTO(requestBody.get("name"),
                    requestBody.get("email"),
                    requestBody.get("password"));

            List<String> profileErrors = userService.profileChangeValidation(userData);
            errorList.addAll(profileErrors);
        if (requestBody.containsKey("password")&&requestBody.containsKey("currentPassword")){
            List<String> passwordErrors = userService.verifyUserPassword(user.getEmail(), requestBody.get("currentPassword"));
            errorList.addAll(passwordErrors);}

            if (errorList.isEmpty()){

                User modifiedUser = userService.modifyUser(username, userData);

                NewUserDTO response = new NewUserDTO(modifiedUser.getId(), modifiedUser.getName(), modifiedUser.getEmail());

                return ResponseEntity.ok(response);
            }

        } else if (requestBody.isEmpty()) {

            return ResponseEntity.status(419).body("A field is required.");

        } else {
            return ResponseEntity.status(419).body("Request contains invalid data.");
        }

        return ResponseEntity.status(419).body(errorList);
    }
}
