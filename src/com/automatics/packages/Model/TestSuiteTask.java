package com.automatics.packages.Model;

import java.beans.*;

import com.automatics.utilities.gsons.testsuite.*;

public class TestSuiteTask 
{
	private PropertyChangeSupport changes = new PropertyChangeSupport(this);
	
	public static final String FIELD_TS_NAME = "id";
    public static final String FIELD_TS_DESCRIPTION= "description";
    public static final String FIELD_TS_IDENTIFIER = "identifier";
    public static final TSGson FIELD_TS_GSON = null;
    
    public String tsName;
    public String tsDesc;
    public String tsIdentifier;
    public TSGson tsGson;
    
    public TestSuiteTask(String tsName)
    {
    	this.tsName = tsName;
    }
    
    public TestSuiteTask(String tsName, String tsDesc, String tsIdentifier,TSGson tsGson)
    {
    	this.tsName = tsName;
    	this.tsDesc = tsDesc;
    	this.tsIdentifier = tsIdentifier;
    	this.tsGson = tsGson;
    }

	public String getTsName() {
		return tsName;
	}

	public String getTsDesc() {
		return tsDesc;
	}

	public String getTsIdentifier() {
		return tsIdentifier;
	}

	public TSGson getTsGson() {
		return tsGson;
	}

	public void setTsName(String tsName) {
		changes.firePropertyChange(FIELD_TS_NAME, this.tsName, this.tsName = tsName);
	}

	public void setTsDesc(String tsDesc) {
		changes.firePropertyChange(FIELD_TS_DESCRIPTION, this.tsDesc, this.tsDesc = tsDesc);
	}

	public void setTsIdentifier(String tsIdentifier) {
		changes.firePropertyChange(FIELD_TS_IDENTIFIER, this.tsIdentifier, this.tsIdentifier = tsIdentifier);
	}

	public void setTsGson(TSGson tsGson) {
		this.tsGson = tsGson;
	}
    
	
	@Override
	public boolean equals(Object obj) {
	        if (this == obj)
	                return true;
	        if (obj == null)
	                return false;
	        if (getClass() != obj.getClass())
	                return false;
	        TestSuiteTask other = (TestSuiteTask) obj;
	        if (!tsName.equals(other.getTsName()))
	                return false;
	        return true;
	}
    
	
	public void addPropertyChangeListener(PropertyChangeListener l) {
        changes.addPropertyChangeListener(l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
        changes.removePropertyChangeListener(l);
	}
    
}
