package com.automatics.utilities.elements;

import java.util.Date;

public class Project {
	private String projectName;
	private String projectDescription;
	private String createBy;
	private Date dateofCreation;
	private String modifiedBy;
	private Date modificationDate;
	
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public String getProjectDescription() {
		return projectDescription;
	}
	public void setProjectDescription(String projectDescription) {
		this.projectDescription = projectDescription;
	}
	public String getCreateBy() {
		return createBy;
	}
	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}
	public Date getDateofCreation() {
		return dateofCreation;
	}
	public void setDateofCreation(Date dateofCreation) {
		this.dateofCreation = dateofCreation;
	}
	public String getModifiedBy() {
		return modifiedBy;
	}
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
	public Date getModificationDate() {
		return modificationDate;
	}
	public void setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
	}
	
	public String toString()
	{
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
		xml = xml + "<projectdescription>\n";
		xml = xml + "\t\t<name>"+projectName+"</name>\n";
		xml = xml + "\t\t<description>"+projectDescription+"</description>\n";
		xml = xml + "\t\t<createdby>"+createBy+"</createdby>\n";
		xml = xml + "\t\t<dateofcreation>"+dateofCreation+"</dateofcreation>\n";
		xml = xml + "\t\t<modifiedby>"+modifiedBy+"</modifiedby>\n";
		xml = xml + "\t\t<modificationdate>"+modificationDate+"</modificationdate>\n";
		xml = xml + "</projectdescription>\n";
		return xml;
	}
}
