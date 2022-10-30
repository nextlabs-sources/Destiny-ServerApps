package com.bluejungle.destiny.version.defaultimpl;

import javax.faces.context.FacesContext;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import com.bluejungle.destiny.version.IVersionBean;

public class InstallerVersionBeanImpl implements IVersionBean {
    private String version;

    public InstallerVersionBeanImpl() {
        this.version = FacesContext.getCurrentInstance().getExternalContext().getInitParameter("application.version");
        if (this.version == null || this.version.isEmpty()) {
            try (InputStream versionInput = Files.newInputStream(Paths.get(System.getProperty("server.config.path"),
                    "bootstrap.properties"))) {
                Properties properties = new Properties();
                properties.load(versionInput);
                this.version = properties.getProperty("application.version", "9.0.0.0");
            } catch (Exception exception) {
                throw new IllegalStateException("Invalid version found", exception);
            }
        }
    }

    /**
     * @see com.bluejungle.destiny.version.IVersionBean#getBuildLabel()
     */
    @Deprecated
    public int getBuildLabel() {
        return 0;
    }

    /**
     * @see com.bluejungle.destiny.version.IVersionBean#getVersion()
     */
    public String getVersion() {
        return this.version;
    }
}
