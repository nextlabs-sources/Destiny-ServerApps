package com.nextlabs.destiny.console.controllers;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import com.nextlabs.destiny.console.annotations.ApiVersion;
import com.nextlabs.destiny.console.dto.common.CollectionDataResponseDTO;
import com.nextlabs.destiny.console.dto.common.ConsoleResponseEntity;
import com.nextlabs.destiny.console.dto.common.ResponseDTO;
import com.nextlabs.destiny.console.dto.common.SimpleResponseDTO;
import com.nextlabs.destiny.console.dto.enrollment.EnrollmentDTO;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.services.EnrollmentMgmtService;
import com.nextlabs.destiny.console.utils.SystemCodes;

import javax.validation.Valid;

import com.bluejungle.pf.destiny.services.PolicyEditorException;

/**
 * Controller to provide enrollment management functions.
 *
 * @author Sachindra Dasun
 */
@RestController
@ApiVersion(1)
@RequestMapping("enrollment")
public class EnrollmentController extends AbstractRestController {

    private static final Logger logger = LoggerFactory.getLogger(EnrollmentController.class);

    @Autowired
    private EnrollmentMgmtService enrollmentMgmtService;

    @Override
    protected Logger getLog() {
        return logger;
    }

    @ApiIgnore
    @ResponseBody
    @PostMapping("save")
    public ConsoleResponseEntity<SimpleResponseDTO<Long>> save(@RequestBody EnrollmentDTO enrollmentDTO)
            throws ConsoleException {
        Long id = enrollmentMgmtService.save(enrollmentDTO);
        enrollmentMgmtService.checkAutoSync(id);
        return ConsoleResponseEntity.get(SimpleResponseDTO.createWithType(
                msgBundle.getText(SystemCodes.DATA_SAVED_SUCCESS.getCode()),
                msgBundle.getText(SystemCodes.DATA_SAVED_SUCCESS.getMessageKey()), id), HttpStatus.OK);
    }

    @ApiIgnore
    @ResponseBody
    @GetMapping("id/{id}")
    public ConsoleResponseEntity<SimpleResponseDTO<EnrollmentDTO>> findById(@PathVariable("id") Long id) {
        validations.assertNotZero(id, "id");
        EnrollmentDTO enrollment = enrollmentMgmtService.findById(id);
        return ConsoleResponseEntity.get(SimpleResponseDTO.createWithType(
                msgBundle.getText(SystemCodes.DATA_FOUND_SUCCESS.getCode()),
                msgBundle.getText(SystemCodes.DATA_FOUND_SUCCESS.getMessageKey()), enrollment), HttpStatus.OK);
    }

    @ApiIgnore
    @ResponseBody
    @GetMapping("all")
    public ConsoleResponseEntity<CollectionDataResponseDTO<EnrollmentDTO>> findAll() {
        List<EnrollmentDTO> enrollments = enrollmentMgmtService.findAll();
        CollectionDataResponseDTO<EnrollmentDTO> response = CollectionDataResponseDTO.createWithType(
                msgBundle.getText(SystemCodes.DATA_FOUND_SUCCESS.getCode()),
                msgBundle.getText(SystemCodes.DATA_FOUND_SUCCESS.getMessageKey()));
        response.setData(enrollments);
        return ConsoleResponseEntity.get(response, HttpStatus.OK);
    }

    @ApiIgnore
    @ResponseBody
    @DeleteMapping("delete/{id}")
    public ConsoleResponseEntity<ResponseDTO> delete(@PathVariable("id") Long id) throws ConsoleException, IOException,
            PolicyEditorException {
        validations.assertNotZero(id, "id");
        if (enrollmentMgmtService.isSyncing(id)) {
            return ConsoleResponseEntity.get(SimpleResponseDTO.create(
                    msgBundle.getText(SystemCodes.TASK_EXECUTION_ERROR.getCode()),
                    msgBundle.getText(SystemCodes.TASK_EXECUTION_ERROR.getMessageKey(), "CONFLICT")), HttpStatus.CONFLICT);
        } else {
            enrollmentMgmtService.remove(id);
            return ConsoleResponseEntity.get(SimpleResponseDTO.create(
                    msgBundle.getText(SystemCodes.DATA_DELETED_SUCCESS.getCode()),
                    msgBundle.getText(SystemCodes.DATA_DELETED_SUCCESS.getMessageKey(), "SUCCESS")), HttpStatus.OK);
        }
    }

    @ApiIgnore
    @ResponseBody
    @GetMapping("sync/{id}")
    public ConsoleResponseEntity<ResponseDTO> sync(@PathVariable("id") Long id) throws ConsoleException {
        validations.assertNotZero(id, "id");
        enrollmentMgmtService.sync(id);
        return ConsoleResponseEntity.get(SimpleResponseDTO.create(
                msgBundle.getText(SystemCodes.ENROLLMENT_DATA_SYNC_STARTED_SUCCESS.getCode()),
                msgBundle.getText(SystemCodes.ENROLLMENT_DATA_SYNC_STARTED_SUCCESS.getMessageKey(), "SUCCESS")), HttpStatus.OK);
    }

    @ApiIgnore
    @ResponseBody
    @PutMapping("upload/{id}")
    public ConsoleResponseEntity<SimpleResponseDTO<String>> upload(@PathVariable("id") Long id,
            @RequestParam("ldifFile") MultipartFile ldifFile,
            @RequestParam("delta") boolean delta) throws IOException {
        if (ldifFile != null) {
            enrollmentMgmtService.upload(id, ldifFile, delta);
        }
        return ConsoleResponseEntity.get(SimpleResponseDTO.createWithType(
                msgBundle.getText(SystemCodes.DATA_FOUND_SUCCESS.getCode()),
                msgBundle.getText(SystemCodes.DATA_FOUND_SUCCESS.getMessageKey()), "SUCCESS"), HttpStatus.OK);
    }

    @ApiIgnore
    @ResponseBody
    @PostMapping("validate")
    public ConsoleResponseEntity<SimpleResponseDTO<String>> validate(@Valid @RequestBody EnrollmentDTO enrollmentDTO) {
        return ConsoleResponseEntity.get(SimpleResponseDTO.createWithType(
                msgBundle.getText(SystemCodes.DATA_VALIDATED_SUCCESS.getCode()),
                msgBundle.getText(SystemCodes.DATA_VALIDATED_SUCCESS.getMessageKey()), "SUCCESS"), HttpStatus.OK);
    }
}
