/*
 * Copyright (C) 2016 - 2026 Spenego Software LLC. All rights reserved.
 *
 * This file is part of Obidos from Spenego Software LLC
 *
 * Obidos is dual-licensed under a commercial license and the GNU
 * Affero General Public License (AGPL) v3.0. For commercial licensing,
 * contact Spenego Software LLC at https://spenego.com/contacts.html.
 *
 * For AGPL licensing terms, see the LICENSE file in the project root
 * or <https://www.gnu.org/licenses/>.
 */

package com.spenego.Obidos.shared.dto;

import java.io.Serializable;

public final class BuildInfoDTO implements Serializable
{

    private static final long serialVersionUID = 1L;
    /**
     * @author spgdev@spenego.com - Mar 25, 2017
     */
    public BuildInfoDTO() { /* default init to null is sufficient */ }
    public BuildInfoDTO(final String changeLog) { this.changeLog = changeLog; }

    public BuildInfoDTO(String projectVersion, String buildNumber, String buildTime, String builtBy, String builtWithJavaVersion, String builtWithJavaVendor, String builtOnOsName,
			String builtOnOsArch, String builtOnOsVersion, String schemaVersion) {
		this.projectVersion = projectVersion;
		this.buildNumber = buildNumber;
		this.buildTime = buildTime;
		this.builtBy = builtBy;
		this.builtWithJavaVersion = builtWithJavaVersion;
		this.builtWithJavaVendor = builtWithJavaVendor;
		this.builtOnOsName = builtOnOsName;
		this.builtOnOsArch = builtOnOsArch;
		this.builtOnOsVersion = builtOnOsVersion;
		this.schemaVersion = schemaVersion;
	}

	private String projectVersion;
    private String buildNumber;
    private String buildTime;
    private String builtBy;
    private String builtWithJavaVersion;
    private String builtWithJavaVendor;
    private String builtOnOsName;
    private String builtOnOsArch;
    private String builtOnOsVersion;
    private String changeLog;
    private String schemaVersion;
    
    public String getProjectVersion()
    {
        return projectVersion;
    }
    public void setProjectVersion(String projectVersion)
    {
        this.projectVersion = projectVersion;
    }
    public String getBuildNumber()
    {
        return buildNumber;
    }
    public void setBuildNumber(String buildNumber)
    {
        this.buildNumber = buildNumber;
    }
    public String getBuildTime()
    {
        return buildTime;
    }
    public void setBuildTime(String buildTime)
    {
        this.buildTime = buildTime;
    }
    public String getBuiltBy()
    {
        return builtBy;
    }
    public void setBuiltBy(String builtBy)
    {
        this.builtBy = builtBy;
    }
    public String getBuiltWithJavaVersion()
    {
        return builtWithJavaVersion;
    }
    public void setBuiltWithJavaVersion(String builtWithJavaVersion)
    {
        this.builtWithJavaVersion = builtWithJavaVersion;
    }
    public String getBuiltWithJavaVendor()
    {
        return builtWithJavaVendor;
    }
    public void setBuiltWithJavaVendor(String builtWithJavaVendor)
    {
        this.builtWithJavaVendor = builtWithJavaVendor;
    }
    public String getBuiltOnOsName()
    {
        return builtOnOsName;
    }
    public void setBuiltOnOsName(String builtOnOsName)
    {
        this.builtOnOsName = builtOnOsName;
    }
    public String getBuiltOnOsArch()
    {
        return builtOnOsArch;
    }
    public void setBuiltOnOsArch(String builtOnOsArch)
    {
        this.builtOnOsArch = builtOnOsArch;
    }
    public String getBuiltOnOsVersion()
    {
        return builtOnOsVersion;
    }
    public void setBuiltOnOsVersion(String builtOnOsVersion)
    {
        this.builtOnOsVersion = builtOnOsVersion;
    }
	public String getChangeLog()
	{
		return changeLog;
	}
	public void setChangeLog(String changeLog)
	{
		this.changeLog = changeLog;
	}
	public String getSchemaVersion()
	{
		return schemaVersion;
	}
	public void setSchemaVersion(String schemaVersion)
	{
		this.schemaVersion = schemaVersion;
	}
}
