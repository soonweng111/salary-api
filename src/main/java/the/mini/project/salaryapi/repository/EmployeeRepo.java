package the.mini.project.salaryapi.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import the.mini.project.salaryapi.domain.Employee;


import java.util.List;

@Repository
public interface EmployeeRepo extends JpaRepository<Employee, String> {

    List<Employee> findAll();

    @Query("SELECT e from Employee e WHERE e.salary BETWEEN ?1 AND ?2")
    List<Employee> findAllBySalaryBetween(double min, double max, Sort sort);

    @Query("SELECT e from Employee e WHERE e.salary BETWEEN ?1 AND ?2")
    List<Employee> findAllBySalaryBetween(double min, double max, Pageable pageable);

}
