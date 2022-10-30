package com.nextlabs.destiny.console.services;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.bluejungle.pf.destiny.services.PolicyEditorException;
import com.nextlabs.destiny.console.dto.enrollment.EnrollmentDTO;
import com.nextlabs.destiny.console.exceptions.ConsoleException;

/**
 * Service for enrollment management functions.
 *
 * @author Sachindra Dasun.
 */
public interface EnrollmentMgmtService {

    List<EnrollmentDTO> findAll();

    EnrollmentDTO findById(Long id);

    void checkAutoSync(Long id) throws ConsoleException;

    Long save(EnrollmentDTO enrollmentDTO) throws ConsoleException;

    void sync(Long id) throws ConsoleException;

    void upload(Long id, MultipartFile multipartFile, boolean delta) throws IOException;

    void remove(Long id) throws ConsoleException, IOException, PolicyEditorException;

    boolean isSyncing(Long id);

}
