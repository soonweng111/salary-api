package the.mini.project.salaryapi.service;

import org.springframework.web.multipart.MultipartFile;
import the.mini.project.salaryapi.domain.EmployeeException;
import the.mini.project.salaryapi.domain.EmployeeRequest;
import the.mini.project.salaryapi.domain.EmployeeResponse;

public interface EmployeeService {
    EmployeeResponse getPersons(EmployeeRequest request) throws EmployeeException;

    int batchUpload(MultipartFile file) throws EmployeeException;
}
