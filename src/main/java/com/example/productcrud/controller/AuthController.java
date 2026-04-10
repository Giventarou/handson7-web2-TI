package com.example.productcrud.controller;

import com.example.productcrud.model.User;
import com.example.productcrud.dto.RegisterRequest;
import com.example.productcrud.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    public AuthController(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model){
        model.addAttribute("registerRequest", new RegisterRequest());
        return "register";
    }

    @PostMapping("/register")
    public String processRegisterForm(@ModelAttribute RegisterRequest registerRequest, RedirectAttributes redirectAttributes){
        //Validasi: username tidak boleh kosong
        if (registerRequest.getUsername() == null || registerRequest.getUsername().isEmpty()){
            redirectAttributes.addFlashAttribute("errorMessage", "Username tidak boleh kosong");
            return "redirect:/";
        }

        //Validasi: password tidak boleh kosong
        if (registerRequest.getPassword() == null || registerRequest.getPassword().isEmpty()){
            redirectAttributes.addFlashAttribute("errorMessage", "Password tidak cocok");
            return "redirect:/register";
        }

        //Validasi: username belum terdaftar
        if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()){
            redirectAttributes.addFlashAttribute("errorMessage", "Username belum terdaftar");
            return "redirect:/register";
        }

        //Simpan user baru dengan password ter-encode
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        userRepository.save(user);

        redirectAttributes.addFlashAttribute("successMessage", "Registrasi berhasil! silahkan login");
        return "redirect:/login";
    }
}
