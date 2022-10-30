package com.nextlabs.destiny.console.services.enrollment;

import com.nextlabs.destiny.console.exceptions.ConsoleException;

/**
 * Service for accessing enrollment service.
 *
 * @author Sachindra Dasun.
 */
public interface EnrollmentServiceWrapperService {

    void remove(String domainName) throws ConsoleException;

    void sync(String domainName) throws ConsoleException;

    void cancelAutoSync(String domainName) throws ConsoleException;
}
