package the.mini.project.salaryapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import the.mini.project.salaryapi.domain.BatchCreateResponse;
import the.mini.project.salaryapi.domain.EmployeeException;
import the.mini.project.salaryapi.domain.EmployeeRequest;
import the.mini.project.salaryapi.domain.EmployeeResponse;
import the.mini.project.salaryapi.service.serviceImpl.EmployeeServiceImpl;
import the.mini.project.salaryapi.utility.Utils;

import java.util.Objects;
import java.util.Optional;

import static the.mini.project.salaryapi.domain.BatchCreateResponse.createResponse;
import static the.mini.project.salaryapi.domain.EmployeeRequest.createEmployeeRequest;

@RestController
@Slf4j
public class EmployeeController {

    private final EmployeeServiceImpl service;

    public EmployeeController(EmployeeServiceImpl service){
        this.service = service;
    }

    @Operation(summary = "Get Users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = EmployeeResponse.class))}),
            @ApiResponse(responseCode = "404", description = "No Records found",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = EmployeeException.class))}),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = EmployeeException.class))}),
    })
    @GetMapping(path = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getUsers(@RequestParam(required = false, name = "min") Optional<Double> min,
                                           @RequestParam(required = false, name = "max") Optional<Double> max,
                                           @RequestParam(required = false, name = "offset") Optional<Integer> offset,
                                           @RequestParam(required = false, name = "limit") Optional<Integer> limit,
                                           @RequestParam(required = false, name = "sort") Optional<String> sort
    ) {
        EmployeeRequest request = createEmployeeRequest(min, max, offset, limit, sort);
        try {
            EmployeeResponse results = service.getPersons(request);
            log.info("Success retrieving request :{}", request);
            return ResponseEntity.ok(Utils.getJson(results));
        } catch (EmployeeException pe) {
            if(pe.getError().equalsIgnoreCase("No Records Found")) {
                log.error("No Records found with this request: {}", request);
                return new ResponseEntity(pe, HttpStatus.NOT_FOUND);
            }
            log.error("Unexpected error with this request: {} :: {}", request, pe.getError());
            return new ResponseEntity(pe, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Unexpected error with this request: {} :: {}", request, e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Upload CSV")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BatchCreateResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BatchCreateResponse.class))}),
    })
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
            if(exception.getError().equalsIgnoreCase("Salary Is Corrupted")) {
                return new ResponseEntity<>(Utils.getJson(createResponse(0)), HttpStatus.BAD_REQUEST);
            }

            return new ResponseEntity<>(Utils.getJson(createResponse(0)), HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }

}
