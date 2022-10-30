package com.bluejungle.destiny.mgmtconsole;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.bluejungle.destiny.appframework.appsecurity.loginmgr.ILoggedInUser;
import com.bluejungle.destiny.webui.framework.context.AppContext;
import com.bluejungle.destiny.mgmtconsole.usersandroles.users.dao.*;
import com.bluejungle.destiny.mgmtconsole.usersandroles.users.dao.impl.*;
import com.bluejungle.destiny.mgmtconsole.usersandroles.users.enumeration.*;
import com.bluejungle.destiny.mgmtconsole.usersandroles.users.utils.JsonUtil;
import com.bluejungle.destiny.mgmtconsole.usersandroles.users.dto.EntityAuditLogDO;
import com.fasterxml.jackson.core.JsonProcessingException;

@WebListener
public class SessionInvalidateListener implements HttpSessionListener {

	static final String APP_CONTEXT_SESSION_ATTR = "destiny.AppContextImpl";

	private static Log log = LogFactory.getLog(SessionInvalidateListener.class);

	@Override
	public void sessionCreated(HttpSessionEvent httpSessionEvent) {
		log.info("Session created");
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
		log.info("Session destroyed");
		log.info(httpSessionEvent);
		HttpSession httpSession = httpSessionEvent.getSession();
		AppContext ctx = null;
		if (httpSession != null) {
			ctx = (AppContext) httpSession.getAttribute(APP_CONTEXT_SESSION_ATTR);
			ILoggedInUser loggedInUser = ctx.getRemoteUser();
			if (loggedInUser != null) {
				try {	
					Map<String, String> audit = new LinkedHashMap<>();
					audit.put("Message", loggedInUser.getUsername() + " has logged out successfully.");

					EntityAuditLogDO auditLog = new EntityAuditLogDO();
					auditLog.setAction(AuditAction.LOGOUT.name());
					auditLog.setActor(loggedInUser.getUsername());
					auditLog.setActorId(loggedInUser.getPrincipalId());
					auditLog.setEntityId(loggedInUser.getPrincipalId());
					auditLog.setEntityType(AuditableEntity.APPLICATION_USER.getCode());
					auditLog.setNewValue(JsonUtil.toJsonString(audit));
					
					EntityAuditLogDAO entityAuditLogDAO = new EntityAuditLogDAOImpl();
					entityAuditLogDAO.create(auditLog);
				} catch (JsonProcessingException e) {
					log.error("Error occurred in creating login audit logs.", e);
				} catch (Exception ex) {
					log.error("Error occurred in creating login audit logs.", ex);
				}
			}
		}
	}
}