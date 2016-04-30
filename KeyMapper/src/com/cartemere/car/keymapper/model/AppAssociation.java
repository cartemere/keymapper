package com.cartemere.car.keymapper.model;

import java.io.Serializable;

/**
 * represent a Key mapping object
 * @author cartemere
 */
public class AppAssociation implements Serializable {

	private static final long serialVersionUID = 20160425L;
	private String keyName = null;
	private String event = null;
	private String appName = null;
	private String appPackageName = null;
	private Boolean isKeyMappingEnabled = null;
	
	public AppAssociation(String keyName, String event, String appName,
			String appPackageName, Boolean isKeyMappingEnabled) {
		super();
		this.keyName = keyName;
		this.event = event;
		this.appName = appName;
		this.appPackageName = appPackageName;
		this.isKeyMappingEnabled = isKeyMappingEnabled;
	}
	
	public String getKeyName() {
		return keyName;
	}
	
	public String getEvent() {
		return event;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getAppPackageName() {
		return appPackageName;
	}
	public void setAppPackageName(String appPackageName) {
		this.appPackageName = appPackageName;
	}
	public Boolean getIsKeyMappingEnabled() {
		return isKeyMappingEnabled;
	}
	public void setIsKeyMappingEnabled(Boolean isKeyMappingEnabled) {
		this.isKeyMappingEnabled = isKeyMappingEnabled;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((appName == null) ? 0 : appName.hashCode());
		result = prime * result
				+ ((appPackageName == null) ? 0 : appPackageName.hashCode());
		result = prime * result + ((event == null) ? 0 : event.hashCode());
		result = prime
				* result
				+ ((isKeyMappingEnabled == null) ? 0 : isKeyMappingEnabled
						.hashCode());
		result = prime * result + ((keyName == null) ? 0 : keyName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AppAssociation other = (AppAssociation) obj;
		if (appName == null) {
			if (other.appName != null)
				return false;
		} else if (!appName.equals(other.appName))
			return false;
		if (appPackageName == null) {
			if (other.appPackageName != null)
				return false;
		} else if (!appPackageName.equals(other.appPackageName))
			return false;
		if (event == null) {
			if (other.event != null)
				return false;
		} else if (!event.equals(other.event))
			return false;
		if (isKeyMappingEnabled == null) {
			if (other.isKeyMappingEnabled != null)
				return false;
		} else if (!isKeyMappingEnabled.equals(other.isKeyMappingEnabled))
			return false;
		if (keyName == null) {
			if (other.keyName != null)
				return false;
		} else if (!keyName.equals(other.keyName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "AppAssociation [keyName=" + keyName + ", event=" + event
				+ ", appName=" + appName + ", appPackageName=" + appPackageName
				+ ", isKeyMappingEnabled=" + isKeyMappingEnabled + "]";
	}

	
	
}
