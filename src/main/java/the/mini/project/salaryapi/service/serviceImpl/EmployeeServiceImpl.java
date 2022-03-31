package the.mini.project.salaryapi.service.serviceImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import the.mini.project.salaryapi.domain.Employee;
import the.mini.project.salaryapi.domain.EmployeeException;
import the.mini.project.salaryapi.domain.EmployeeRequest;
import the.mini.project.salaryapi.domain.EmployeeResponse;
import the.mini.project.salaryapi.repository.EmployeeRepo;
import the.mini.project.salaryapi.service.EmployeeService;
import the.mini.project.salaryapi.utility.PageBasedOnOffset;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class EmployeeServiceImpl implements EmployeeService {
    private static final String NO_RECORDS_FOUND = "No Employee Found.";
    private final EmployeeRepo repo;


    public EmployeeServiceImpl(EmployeeRepo repo) {
        this.repo = repo;
    }

    @Override
    public EmployeeResponse getPersons(EmployeeRequest request) throws EmployeeException {
        boolean offSetFlag = false;
        boolean limitFlag = false;
        boolean sortFlag = false;

        if (request.getOffset() != 0) {
            offSetFlag = true;
        }

        if (request.getLimit() != 0) {
            limitFlag = true;
        }

        if (!request.getSort().isEmpty()) {
            sortFlag = true;
        }

        List<Employee> results = getResults(request, offSetFlag, limitFlag, sortFlag);

        if (Objects.isNull(results) || results.isEmpty()) {
            throw new EmployeeException(NO_RECORDS_FOUND);
        }

        return EmployeeResponse.createPersonResponse(results);
    }

    @Override
    public int batchUpload(MultipartFile file) throws EmployeeException {
            List<Employee> employeeList = parsedCSVFile(file);
            repo.saveAll(employeeList);
            return 1;
    }


    private List<Employee> getResults(EmployeeRequest request, boolean offSetFlag, boolean limitFlag, boolean sortFlag) {
        double minSalary = request.getMin();
        double maxSalary = request.getMax();

        if (!limitFlag && !offSetFlag) {
            if (sortFlag) {
                return repo.findAllBySalaryBetween(minSalary, maxSalary, Sort.by(request.getSort().toLowerCase()));
            } else {
                return repo.findAllBySalaryBetween(minSalary, maxSalary, Sort.unsorted());
            }
        } else if (!limitFlag && offSetFlag) {
            List<Employee> result;
            if (sortFlag) {
                result = repo.findAllBySalaryBetween(minSalary, maxSalary,
                        Sort.by(request.getSort().toLowerCase()));
            } else {
                result = repo.findAllBySalaryBetween(minSalary, maxSalary, Sort.unsorted());
            }
            return result.subList(request.getOffset(), result.size());
        } else {
            int offset = request.getOffset();
            int limit = request.getLimit();
            Pageable page;
            if (sortFlag) {
                page = new PageBasedOnOffset(offset, limit, Sort.by(request.getSort().toLowerCase()));
            } else {
                page = new PageBasedOnOffset(offset, limit, Sort.unsorted());
            }
            return repo.findAllBySalaryBetween(request.getMin(), request.getMax(), page);
        }
    }

    private List<Employee> parsedCSVFile(final MultipartFile file) throws EmployeeException {
        final List<Employee> employeeList = new LinkedList<>();
        try {
           try {
               try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
                   if (br.readLine() == null) {
                       throw new EmployeeException("Error Reading File");
                   }
                   String line;
                   while ((line = br.readLine()) != null) {
                       final String[] data = line.split(",");
                       if (data.length > 2) {
                           throw new EmployeeException("File Error");
                       }
                       final Employee employee = new Employee();
                       employee.setName(data[0]);
                       double salary = (Double.parseDouble(data[1]));
                       if (salary < 0.0) {
                           salary = 0.00;
                       }
                       employee.setSalary(salary);
                       employeeList.add(employee);
                   }
                   br.close();
                   return employeeList;
               }
           } catch (NumberFormatException numberFormatException) {
                log.error("Salary Is Corrupted");
                throw new EmployeeException("Salary Is Corrupted");
            }
        } catch (IOException io) {
            log.error("Error Reading File");
            throw new EmployeeException("Error Reading File");
        }
    }
}
