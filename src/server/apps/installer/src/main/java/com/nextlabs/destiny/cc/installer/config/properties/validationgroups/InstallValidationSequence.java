package com.nextlabs.destiny.cc.installer.config.properties.validationgroups;

import javax.validation.GroupSequence;
import javax.validation.groups.Default;

/**
 * Tagging interface for install validation sequence.
 *
 * @author Sachindra Dasun
 */
@GroupSequence({Default.class, CaCertValidation.class, DbValidation.class})
public interface InstallValidationSequence {

}
