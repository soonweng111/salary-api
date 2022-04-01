package the.mini.project.salaryapi.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;
import the.mini.project.salaryapi.domain.Employee;
import the.mini.project.salaryapi.domain.EmployeeException;
import the.mini.project.salaryapi.domain.EmployeeRequest;
import the.mini.project.salaryapi.domain.EmployeeResponse;
import the.mini.project.salaryapi.repository.EmployeeRepo;
import the.mini.project.salaryapi.service.serviceImpl.EmployeeServiceImpl;
import the.mini.project.salaryapi.utility.Utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@SpringBootTest()
@ActiveProfiles("test")
public class EmployeeServiceImplTest {

    @Autowired
    private EmployeeRepo repo;

    @Autowired
    private EmployeeServiceImpl service;

    @BeforeEach
    public void init() {
        repo.save(new Employee("John", 3000.00));
        repo.save(new Employee("Daniel", 2500.10));
        repo.save(new Employee("Johnathon", 4000.00));
        repo.save(new Employee("Andrew", 1500.00));
        repo.save(new Employee("Anthony", 1000.00));
        repo.save(new Employee("Intern", 500.00));
    }

    @AfterEach
    public void cleanup() {
        repo.deleteAll();
    }

    @Test
    public void getEmployee_LimitFlagTrue_OffsetFlagTrue_SortFlagTrue_Success() {

        EmployeeRequest request = EmployeeRequest.createEmployeeRequest(Optional.of(0.00), Optional.of(4000.0),
                Optional.of(1), Optional.of(2), Optional.of("name"));

        try {
            EmployeeResponse response = service.getPersons(request);
            System.out.println(Utils.getJson(response));
            Assertions.assertEquals(2, response.getResults().size());
        } catch (EmployeeException ee) {
            Assertions.fail();
        }
    }

    @Test
    public void getEmployee_LimitFlagTrue_OffsetFlagTrue_SortFlagFalse_Success() {

        EmployeeRequest request = EmployeeRequest.createEmployeeRequest(Optional.of(0.00), Optional.of(4000.0),
                Optional.of(1), Optional.of(2), Optional.of(""));

        try {
            EmployeeResponse response = service.getPersons(request);
            System.out.println(Utils.getJson(response));
            Assertions.assertEquals(2, response.getResults().size());
        } catch (EmployeeException ee) {
            Assertions.fail();
        }
    }

    @Test
    public void getEmployee_LimitFlagTrue_OffsetFlagTrue_SortFlagTrue_Fail_DueToHighOffset() {
        EmployeeRequest request = EmployeeRequest.createEmployeeRequest(Optional.of(0.00), Optional.of(4000.0),
                Optional.of(6), Optional.of(1), Optional.of(""));

        try {
            EmployeeResponse response = service.getPersons(request);
            System.out.println(Utils.getJson(response));
            Assertions.fail();
        } catch (EmployeeException ee) {
            Assertions.assertTrue(ee.getError().equalsIgnoreCase("No Employee Found."));
        }
    }

    @Test
    public void getEmployee_LimitFlagFalse_OffsetFlagTrue_SortFlagTrue_Success() {
        EmployeeRequest request = EmployeeRequest.createEmployeeRequest(Optional.of(0.00), Optional.of(4000.0),
                Optional.of(3), Optional.of(0), Optional.of("name"));

        try {
            EmployeeResponse response = service.getPersons(request);
            Assertions.assertEquals(3, response.getResults().size());
        } catch (EmployeeException ee) {
            Assertions.fail();
        }
    }

    @Test
    public void getEmployee_LimitFlagFalse_OffsetFlagTrue_SortFlagTrue_False() {
        EmployeeRequest request = EmployeeRequest.createEmployeeRequest(Optional.of(0.00), Optional.of(4000.0),
                Optional.of(3), Optional.of(0), Optional.of(""));

        try {
            EmployeeResponse response = service.getPersons(request);
            Assertions.assertEquals(3, response.getResults().size());
        } catch (EmployeeException ee) {
            Assertions.fail();
        }
    }

    @Test
    public void getEmployee_LimitFlagFalse_OffsetFlagFalse_SortFlagTrue_Success() {
        EmployeeRequest request = EmployeeRequest.createEmployeeRequest(Optional.of(0.00), Optional.of(4000.0),
                Optional.of(0), Optional.of(0), Optional.of("name"));

        try {
            EmployeeResponse response = service.getPersons(request);
            Assertions.assertEquals(6, response.getResults().size());
        } catch (EmployeeException ee) {
            Assertions.fail();
        }
    }

    @Test
    public void getEmployee_LimitFlagFalse_OffsetFlagFalse_SortFlagFalse_Success() {
        EmployeeRequest request = EmployeeRequest.createEmployeeRequest(Optional.of(0.00), Optional.of(4000.0),
                Optional.of(0), Optional.of(0), Optional.of(""));

        try {
            EmployeeResponse response = service.getPersons(request);
            Assertions.assertEquals(6, response.getResults().size());
        } catch (EmployeeException ee) {
            Assertions.fail();
        }
    }

    @Test
    public void batchUpload_File_Success() {
        try {
            MultipartFile file = new MockMultipartFile("salaryCsv.csv", "salaryCsv.csv",
                    MediaType.MULTIPART_FORM_DATA_VALUE,
                    new FileInputStream("src/test/resources/data/salaryCsv.csv"));

            int result = service.batchUpload(file);
            EmployeeRequest request = EmployeeRequest.createEmployeeRequest(Optional.of(0.00), Optional.of(4000.0),
                    Optional.of(0), Optional.of(0), Optional.of("name"));

            List<Employee> resultList = service.getPersons(request).getResults();
            for(Employee e : resultList) {
                System.out.println(e.toString());
            }
            Assertions.assertEquals(1, result);
            Assertions.assertEquals(12, resultList.size());
        } catch (IOException io) {
            Assertions.fail();
        } catch (EmployeeException ee) {
            System.out.println(ee.getError() + " " + ee.getMessage());
            Assertions.fail();
        }
    }

    @Test
    public void batchUpload_EmptyCsv_Fail() {
        try {
            MultipartFile file = new MockMultipartFile("emptyCsv.csv", "emptyCsv.csv",
                    MediaType.MULTIPART_FORM_DATA_VALUE,
                    new FileInputStream("src/test/resources/data/emptyCsv.csv"));

            service.batchUpload(file);
            Assertions.fail();
        } catch (EmployeeException ee) {
            int size = repo.findAll().size();
            Assertions.assertEquals("Error Reading File", ee.getError());
            Assertions.assertEquals(6,size);
        } catch (IOException io) {
            Assertions.fail();
        }
    }

    @Test
    public void batchUpload_BrokenCsv_Fail() {
        try {
            MultipartFile file = new MockMultipartFile("brokenSalaryCsv.csv", "brokenSalaryCsv.csv",
                    MediaType.MULTIPART_FORM_DATA_VALUE,
                    new FileInputStream("src/test/resources/data/brokenSalaryCsv.csv"));

            service.batchUpload(file);

            Assertions.fail();
        } catch (EmployeeException ee) {
            int size = repo.findAll().size();
            Assertions.assertEquals("Salary Is Corrupted", ee.getError());
            Assertions.assertEquals(6, size);
        } catch (IOException io) {
            Assertions.fail();
        }
    }

    @Test
    public void batchUpload_AdditionalColumn_Fail() {
        try {
            MultipartFile file = new MockMultipartFile("additionalColumnCsv.csv", "additionalColumnCsv.csv",
                    MediaType.MULTIPART_FORM_DATA_VALUE,
                    new FileInputStream("src/test/resources/data/additionalColumnCsv.csv"));

            service.batchUpload(file);

            Assertions.fail();
        } catch (EmployeeException ee) {
            int size = repo.findAll().size();
            Assertions.assertEquals("File Error", ee.getError());
            Assertions.assertEquals(6, size);
        } catch (IOException io) {
            Assertions.fail();
        }
    }



}
