package com.example.employeemanagement.employee;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.employeemanagement.common.DuplicateResourceException;
import com.example.employeemanagement.department.DepartmentResponse;
import com.example.employeemanagement.department.DepartmentService;
import com.example.employeemanagement.employee.dto.EmployeeResponse;

import jakarta.validation.Valid;

// Lab 6: trang HTML (Thymeleaf). @Controller (không phải @RestController): giá trị trả về là TÊN VIEW
// trong templates/ (≈ render "employees/list"). Dùng chung EmployeeService với REST API.
@Controller
@RequestMapping("/employees")
public class EmployeePageController {

    private final EmployeeService employeeService;
    private final DepartmentService departmentService;

    public EmployeePageController(EmployeeService employeeService, DepartmentService departmentService) {
        this.employeeService = employeeService;
        this.departmentService = departmentService;
    }

    // Chạy trước MỌI handler trong controller này, đưa danh sách phòng ban vào Model cho ô <select>
    // (≈ before_action :load_departments)
    @ModelAttribute("departments")
    List<DepartmentResponse> departments() {
        return departmentService.list();
    }

    @GetMapping
    String index() {
        return "redirect:/employees/list";
    }

    // GET /employees/list
    @GetMapping("/list")
    String list(Model model) {
        model.addAttribute("employees", employeeService.search(null, null));
        return "employees/list";
    }

    // GET /employees/add → form trống
    @GetMapping("/add")
    String addForm(Model model) {
        model.addAttribute("employeeForm", new EmployeeForm());
        return "employees/form";
    }

    // POST /employees/add. BindingResult PHẢI đứng ngay sau @Valid, nếu không lỗi validation
    // sẽ thành exception 400 thay vì render lại form.
    @PostMapping("/add")
    String add(@Valid @ModelAttribute("employeeForm") EmployeeForm form, BindingResult result,
               RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return "employees/form";
        }
        try {
            EmployeeResponse created = employeeService.create(form.toRequest());
            // PRG: redirect sau POST để F5 không gửi lại form; flash ≈ flash[:notice]
            redirect.addFlashAttribute("message", "Đã thêm nhân viên " + created.name() + " (" + created.code() + ")");
            return "redirect:/employees/list";
        } catch (DuplicateResourceException ex) {
            result.rejectValue("email", "duplicate", ex.getMessage());
            return "employees/form";
        }
    }

    // GET /employees/search?name=an&departmentId=1
    // departmentId để trống ("") → Spring chuyển thành null → không lọc theo phòng ban.
    @GetMapping("/search")
    String search(@RequestParam(required = false) String name,
                  @RequestParam(required = false) Long departmentId,
                  Model model) {
        boolean searched = name != null || departmentId != null;
        model.addAttribute("name", name);
        model.addAttribute("departmentId", departmentId);
        model.addAttribute("searched", searched);
        model.addAttribute("employees", searched ? employeeService.search(name, departmentId) : List.of());
        return "employees/search";
    }
}
