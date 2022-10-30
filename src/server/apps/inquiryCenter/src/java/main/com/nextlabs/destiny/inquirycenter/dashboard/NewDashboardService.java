/**
 * 
 */
package com.nextlabs.destiny.inquirycenter.dashboard;

import java.util.List;

import net.sf.hibernate.HibernateException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.richfaces.json.JSONException;
import org.richfaces.json.JSONObject;

import com.nextlabs.destiny.configclient.Config;
import com.nextlabs.destiny.configclient.ConfigClient;
import com.nextlabs.destiny.container.shared.inquirymgr.hibernateimpl.AttributeColumnMappingDAOImpl;
import com.nextlabs.destiny.container.shared.inquirymgr.hibernateimpl.AttributeColumnMappingDO;
import com.nextlabs.destiny.inquirycenter.SharedUtils;

/**
 * @author nnallagatla
 *
 */
public class NewDashboardService {
	
	public static final Log LOG = LogFactory.getLog(NewDashboardService.class);
	private static final Config showSharePointConfig = ConfigClient.get("show.sharepoint");
	
	/**
	 * 
	 * @return
	 */
	public static JSONObject getColumnNameAttributeNameMapping()
	{
		JSONObject object = new JSONObject();
		
		try {
			List<AttributeColumnMappingDO> mappingList = AttributeColumnMappingDAOImpl.list();
			
			for (AttributeColumnMappingDO mapping : mappingList)
			{
				if (mapping.getColumnName() != null)
				{
					object.put(mapping.getColumnName(), SharedUtils.getAttributeColumnMappingJSON(mapping));
				}
			}
			return object;
			
		} catch (HibernateException e) {
			LOG.error(e);
		} catch (JSONException e) {
			LOG.error(e);
		}
		return object;
	}
	
	public static boolean showSharePointReports() {
        return showSharePointConfig.toBoolean();
	}
}
