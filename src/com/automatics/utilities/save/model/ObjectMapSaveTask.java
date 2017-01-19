package com.automatics.utilities.save.model;

import com.automatics.utilities.gsons.objectmap.OMGson;

public class ObjectMapSaveTask 
{
	private String omName;
	private OMGson omGson;
	
	public ObjectMapSaveTask(String omName, OMGson omGson)
	{
		this.omName = omName;
		this.omGson = omGson;
	}
	
	public String getOmName() {
		return omName;
	}
	public void setOmName(String omName) {
		this.omName = omName;
	}
	public OMGson getOmGson() {
		return omGson;
	}
	public void setOmGson(OMGson omGson) {
		this.omGson = omGson;
	}
	
}
