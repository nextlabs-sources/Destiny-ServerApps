package com.nextlabs.destiny.console.services;

import java.util.Date;
import java.util.List;

import com.bluejungle.pf.destiny.services.PolicyEditorClient;
import com.nextlabs.destiny.console.dto.policymgmt.PushResultDTO;

/**
 * The interface <code>DPSProxyService</code> can be used to access DPS service.
 *
 * @author Sachindra Dasun
 */
public interface DPSProxyService {

    List<PushResultDTO> schedulePush(Date scheduleTime);

    PolicyEditorClient getPolicyEditorClient();

}
