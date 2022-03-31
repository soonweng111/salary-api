package the.mini.project.salaryapi.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EmployeeTest {

    @Test
    public void createPersonTest_Constructor() {
        Employee p = new Employee("testUser", 2000.00);

        Assertions.assertEquals("testUser", p.getName());
        Assertions.assertEquals(2000.00, p.getSalary());

    }

    @Test
    public void createPersonTest_Setter(){
        Employee p = new Employee();
        p.setName("testUser");
        p.setSalary(2000.00);

        Assertions.assertEquals("testUser", p.getName());
        Assertions.assertEquals(2000.00, p.getSalary());
    }

    @Test
    public void employeeToStringTest(){
        Employee p = new Employee("testUser", 2000);
        Assertions.assertTrue(p.toString().contains("testUser"));
    }
}
