package com.nextlabs.destiny.console.services.authentication;

import com.nextlabs.destiny.console.dto.authentication.AuthHandlerDetail;
import com.nextlabs.destiny.console.exceptions.ConsoleException;

public interface DelegationResourceService {

    void configure(AuthHandlerDetail handlerDetail)
                    throws ConsoleException;

    void cleanUp() throws ConsoleException;
}
