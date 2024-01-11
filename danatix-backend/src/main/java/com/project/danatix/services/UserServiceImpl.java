package com.project.danatix.services;

import com.project.danatix.models.DTOs.UserDataReceivedDTO;
import com.project.danatix.models.Role;
import com.project.danatix.models.User;
import com.project.danatix.repositories.UserRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.beans.Encoder;
import java.security.SecureRandom;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void validateUserEmail(UserDataReceivedDTO userData) throws IllegalArgumentException {

        if (getUserByEmail(userData.getEmail()) != null) {
            throw new IllegalArgumentException("Email is already taken.");
        }
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findUserByEmail(email).orElse(null);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findUserByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Email or password is incorrect."));
    }

    @Override
    public User createNewUser(UserDataReceivedDTO userData) {
        User newUser = new User(userData);
        newUser.setPassword(passwordEncoder.encode(userData.getPassword()));
        newUser.setRole(Role.ROLE_USER);
        generateEmailVerificationTokenForUser(newUser);

        return userRepository.save(newUser);
    }

    @Override
    public List<String> profileChangeValidation(UserDataReceivedDTO userData) {

        List<String> errorList = new ArrayList<>();

        if (!(userData.getEmail() == null)) {

            if (userRepository.existsByEmail(userData.getEmail())) {
                errorList.add("Email is already taken.");
            }
        }

        if (!(userData.getPassword() == null)) {

            if (userData.getPassword().length() < 8) {
                errorList.add("Password must be at least 8 characters.");
            }
        }
        return errorList;
    }

    @Override
    public User modifyUser(String username, UserDataReceivedDTO userData) throws UsernameNotFoundException {

        User userToChange = userRepository.findUserByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("There's no user with this email."));

        if (!(userData.getName() == null)) {

            userToChange.setName(userData.getName());
        }

        if (!(userData.getEmail() == null)) {
            userToChange.setEmail(userData.getEmail());
        }

        if (!(userData.getPassword() == null)) {

            userToChange.setPassword(passwordEncoder.encode(userData.getPassword()));
        }

        return userRepository.save(userToChange);
    }

    @Override
    public boolean doesEmailExist(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public void generateEmailVerificationTokenForUser(User user) {
        final int SEVENTY_TWO_HOURS_IN_MILLISECONDS = 1000 * 60 * 72;

        SecureRandom secureRandom = new SecureRandom();
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        String token = new String(Base64.getEncoder().encode(bytes));

        user.setEmailVerificationToken(token);
        user.setEmailTokenExpiration(new Date(System.currentTimeMillis() + SEVENTY_TWO_HOURS_IN_MILLISECONDS));

    }

    @Override
    public void verifyUserEmail(String token) {

        Optional<User> user = userRepository.findUserByEmailVerificationToken(token);

        if (user.isEmpty()) {
            throw new IllegalArgumentException("Token does not match any registered email.");
        }

        if (user.get().isEmailVerified()) {
            throw new IllegalArgumentException("Token has already been used, your email is already verified.");
        }

        if (user.get().getEmailTokenExpiration().before(new Date())) {
            throw new IllegalArgumentException("Token has expired.");
        }

        user.get().setEmailVerified(true);
        userRepository.save(user.get());
    }

    @Override
    public List<String> verifyUserPassword(String email, String currentPassword) {
        List<String> errorList = new ArrayList<>();
        Optional<User> userOptional = userRepository.findUserByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String storedPassword = user.getPassword();
            if (!passwordEncoder.matches(currentPassword, storedPassword)) {
                errorList.add("Old password does not match!");
            }
        } else {
            errorList.add("User was not found! 2");
        }
        return errorList;
    }

    public int removeDuplicates(int[] nums) {
        if (nums.length==0){
            return 0;
        }
        int k=1;

        for (int i=1;i<nums.length;i++){
            if (nums[i]!=nums[i-1]){
                nums[k]=nums[i];
                k++;
            }
        }

        return k;
    }
}
