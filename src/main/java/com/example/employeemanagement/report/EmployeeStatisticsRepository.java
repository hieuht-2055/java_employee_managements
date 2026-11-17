package com.example.employeemanagement.report;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import com.example.employeemanagement.employee.Employee;

// Lab 10: repository CHỈ ĐỌC cho báo cáo. Kế thừa marker interface Repository<T, ID> (không phải JpaRepository)
// → chỉ có đúng các method khai báo dưới đây, không lộ save/delete ra tầng báo cáo.
public interface EmployeeStatisticsRepository extends Repository<Employee, Long> {

    @Query("select count(e) from Employee e")
    long countEmployees();

    @Query("select count(d) from Department d")
    long countDepartments();

    // LEFT JOIN từ Department để phòng ban chưa có ai vẫn hiện với số 0 (inner join sẽ làm mất dòng đó).
    // "join Employee e on e.department = d": join theo điều kiện tự viết (JPA 2.1+), vì Department
    // không có @OneToMany employees. count(e.id) đếm 0 khi e null; count(e) / count(*) sẽ ra 1.
    // select new ...: Hibernate gọi constructor của record, không cần Object[] rồi tự ép kiểu.
    // (≈ Department.left_joins(:employees).group(:id, :name).count("employees.id"))
    @Query("""
        select new com.example.employeemanagement.report.DepartmentEmployeeCount(d.id, d.name, count(e.id))
        from Department d left join Employee e on e.department = d
        group by d.id, d.name
        order by count(e.id) desc, d.name
        """)
    List<DepartmentEmployeeCount> countEmployeesByDepartment();
}
