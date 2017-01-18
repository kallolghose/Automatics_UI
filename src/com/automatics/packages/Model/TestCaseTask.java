package com.automatics.packages.Model;

import java.beans.*;

import com.automatics.utilities.gsons.testcase.TCGson;


public class TestCaseTask 
{
	private PropertyChangeSupport changes = new PropertyChangeSupport(this);
	
	public static final String FIELD_TC_NAME = "id";
    public static final String FIELD_TC_DESCRIPTION= "description";
    public static final String FIELD_TC_TYPE= "tctype";
    public static final String FIELD_TC_IDENTIFIER = "identifier";
    public static final TCGson FIELD_TC_GSON = null;
    
    public String tcName;
    public String tcDesc;
    public String tcType;
    public String tcIdentifier;
    public TCGson tcGson;
    
    public TestCaseTask(String tcName)
    {
    	this.tcName = tcName;
    }
    
    public TestCaseTask(String tcName, String tcDesc,String tcType, String tcIdentifier, TCGson tcGson)
    {
    	this.tcName = tcName;
    	this.tcDesc = tcDesc;
    	this.tcType = tcType;
    	this.tcIdentifier = tcIdentifier;
    	this.tcGson = tcGson;
    }

	public String getTcName() {
		return tcName;
	}

	public String getTcDesc() {
		return tcDesc;
	}

	public String getTcType() {
		return tcType;
	}

	public String getTcIdentifier() {
		return tcIdentifier;
	}

	public TCGson getTcGson() {
		return tcGson;
	}
    

	
	public void setTcName(String tcName) {
		changes.firePropertyChange(FIELD_TC_NAME, this.tcName, this.tcName = tcName);
	}

	public void setTcDesc(String tcDesc) {
		changes.firePropertyChange(FIELD_TC_DESCRIPTION, this.tcDesc, this.tcDesc = tcDesc);
	}

	public void setTcType(String tcType) {
		changes.firePropertyChange(FIELD_TC_TYPE, this.tcType, this.tcType = tcType);
	}

	public void setTcIdentifier(String tcIdentifier) {
		changes.firePropertyChange(FIELD_TC_IDENTIFIER, this.tcIdentifier, this.tcIdentifier = tcIdentifier);
	}

	public void setTcGson(TCGson tcGson) {
		this.tcGson = tcGson;
	}

	@Override
	public boolean equals(Object obj) {
	        if (this == obj)
	                return true;
	        if (obj == null)
	                return false;
	        if (getClass() != obj.getClass())
	                return false;
	        TestCaseTask other = (TestCaseTask) obj;
	        if (!tcName.equals(other.getTcName()))
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
