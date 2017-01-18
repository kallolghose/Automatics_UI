package com.automatics.packages.Model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import com.automatics.utilities.gsons.objectmap.OMGson;

public class ObjectMapTask 
{
	private PropertyChangeSupport changes = new PropertyChangeSupport(this);
	
	public static final String FIELD_OM_NAME = "id";
    public static final String FIELD_OM_DESCRIPTION= "description";
    public static final String FIELD_OM_IDENTIFIER = "identifier";
    public static final OMGson FIELD_OM_GSON = null;
    
    public String omName;
    public String omDesc;
    public String omIdentifier;
    public OMGson omGson;
    
    public ObjectMapTask(String omName)
    {
    	this.omName = omName;
    }
    
    public ObjectMapTask(String omName,String omDesc,String omIdentifier, OMGson omGson)
    {
    	this.omName = omName;
    	this.omDesc = omDesc;
    	this.omIdentifier = omIdentifier;
    	this.omGson = omGson;
    }

	public String getOmName() {
		return omName;
	}

	public void setOmName(String omName) {
		changes.firePropertyChange(FIELD_OM_NAME, this.omName, this.omName = omName);
	}

	public String getOmDesc() {
		return omDesc;
	}

	public void setOmDesc(String omDesc) {
		changes.firePropertyChange(FIELD_OM_DESCRIPTION, this.omDesc, this.omDesc = omDesc);
	}

	public String getOmIdentifier() {
		return omIdentifier;
	}

	public void setOmIdentifier(String omIdentifier) {
		changes.firePropertyChange(FIELD_OM_IDENTIFIER, this.omIdentifier, this.omIdentifier = omIdentifier);
	}

	public OMGson getOmGson() {
		return omGson;
	}

	public void setOmGson(OMGson omGson) {
		this.omGson = omGson;
	}
	
	@Override
	public boolean equals(Object obj) {
	        if (this == obj)
	                return true;
	        if (obj == null)
	                return false;
	        if (getClass() != obj.getClass())
	                return false;
	        ObjectMapTask other = (ObjectMapTask) obj;
	        if (!omName.equals(other.getOmName()))
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
