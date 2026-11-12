package com.example.employeemanagement.employee;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

import com.example.employeemanagement.common.UtilityService;

// Lab 2 + 3: bean @Repository giữ dữ liệu in-memory (mất khi restart).
// Bean là singleton và nhiều request chạy song song trên nhiều thread
// → dùng ConcurrentHashMap + AtomicLong thay cho HashMap + long.
// Lab 4 sẽ thay class này bằng interface EmployeeRepository extends JpaRepository.
@Repository
public class InMemoryEmployeeRepository {

    private final Map<Long, Employee> store = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong();
    private final UtilityService utilityService;

    public InMemoryEmployeeRepository(UtilityService utilityService) {
        this.utilityService = utilityService;
    }

    public Employee save(Employee employee) {
        long id = sequence.incrementAndGet();
        employee.assignIdentity(id, utilityService.generateEmployeeCode(id));
        store.put(id, employee);
        return employee;
    }

    public List<Employee> findAll() {
        return store.values().stream()
            .sorted(Comparator.comparing(Employee::getId))
            .toList();
    }

    public Optional<Employee> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }
}
