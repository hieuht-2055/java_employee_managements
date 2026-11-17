package com.example.employeemanagement.auth;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.employeemanagement.common.DuplicateResourceException;

import jakarta.validation.Valid;

// Lab 9: trang đăng nhập / đăng ký cho giao diện web.
// POST /login KHÔNG viết ở đây: UsernamePasswordAuthenticationFilter của Spring Security xử lý
// (≈ Devise::SessionsController#create), controller chỉ hiển thị form.
@Controller
public class AuthPageController {

    private final AuthService authService;

    public AuthPageController(AuthService authService) {
        this.authService = authService;
    }

    // /login?error (sai mật khẩu), /login?logout (vừa đăng xuất): template tự đọc tham số ${param.xxx}
    @GetMapping("/login")
    String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    String registerForm(Model model) {
        model.addAttribute("registerForm", new RegisterForm());
        return "auth/register";
    }

    @PostMapping("/register")
    String register(@Valid @ModelAttribute("registerForm") RegisterForm form, BindingResult result,
                    RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return "auth/register";
        }
        try {
            authService.register(form.getUsername(), form.getPassword());
        } catch (DuplicateResourceException ex) {
            result.rejectValue("username", "duplicate", ex.getMessage());
            return "auth/register";
        }
        redirect.addFlashAttribute("message", "Đăng ký thành công, hãy đăng nhập.");
        return "redirect:/login";
    }
}
