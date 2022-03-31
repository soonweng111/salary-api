package the.mini.project.salaryapi.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import the.mini.project.salaryapi.domain.EmployeeException;
import the.mini.project.salaryapi.domain.EmployeeRequest;
import the.mini.project.salaryapi.domain.EmployeeResponse;
import the.mini.project.salaryapi.service.serviceImpl.EmployeeServiceImpl;
import the.mini.project.salaryapi.utility.Utils;

import java.util.Objects;
import java.util.Optional;

import static the.mini.project.salaryapi.domain.BatchCreateResponse.createResponse;
import static the.mini.project.salaryapi.domain.EmployeeRequest.createPersonRequest;

@RestController
@Slf4j
public class EmployeeController {

    private final EmployeeServiceImpl service;

    public EmployeeController(EmployeeServiceImpl service){
        this.service = service;
    }

    @GetMapping(path = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getUsers(@RequestParam(required = false, name = "min") Optional<Double> min,
                                           @RequestParam(required = false, name = "max") Optional<Double> max,
                                           @RequestParam(required = false, name = "offset") Optional<Integer> offset,
                                           @RequestParam(required = false, name = "limit") Optional<Integer> limit,
                                           @RequestParam(required = false, name = "sort") Optional<String> sort
    ) {
        EmployeeRequest request = createPersonRequest(min, max, offset, limit, sort);
        try {
            EmployeeResponse results = service.getPersons(request);
            log.info("Success retrieving request :{}", request);
            return ResponseEntity.ok(Utils.getJson(results));
        } catch (EmployeeException pe) {
            if(pe.getError().equalsIgnoreCase("No Records Found")) {
                log.error("No Records found with this request: {}", request);
                return new ResponseEntity<>(String.format("{\"error\":\"%s\"}", pe.getError()), HttpStatus.NOT_FOUND);
            }
            log.error("Unexpected error with this request: {} :: {}", request, pe.getError());
            return new ResponseEntity<>(String.format("{\"error\":\"%s\"}", pe.getError()), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Unexpected error with this request: {} :: {}", request, e.getMessage());
            return new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createUsers(@RequestPart MultipartFile document) {
        if(Objects.isNull(document) || document.isEmpty()){
            log.error("File is null or empty :{}", document.getName());
            return new ResponseEntity<>(Utils.getJson(createResponse(0)), HttpStatus.BAD_REQUEST);
        }
        try {
            int result = service.batchUpload(document);
            if (result == 1) {
                log.info("File Upload success: {}", document.getName());
                return new ResponseEntity<>(Utils.getJson(createResponse(result)), HttpStatus.OK);
            }
            log.error("File upload fail: {}", document.getName());
            return new ResponseEntity<>(Utils.getJson(createResponse(0)), HttpStatus.BAD_REQUEST);
        } catch (EmployeeException exception) {
            return new ResponseEntity<>(Utils.getJson(createResponse(0)), HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }

}
