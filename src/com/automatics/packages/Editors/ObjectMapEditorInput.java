package com.automatics.packages.Editors;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

public class ObjectMapEditorInput implements IEditorInput {

	private String id;
	
	public ObjectMapEditorInput(String id)
	{
		this.id = id;
	}
	
	public String getId()
	{
		return id;
	}
	
	public Object getAdapter(Class adapter) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean exists() {
		// TODO Auto-generated method stub
		return true;
	}

	public ImageDescriptor getImageDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return id + "_objectmap";
	}

	public IPersistableElement getPersistable() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getToolTipText() {
		// TODO Auto-generated method stub
		return "Object Map";
	}

	public boolean equals(Object obj) {
        if (this == obj)
                return true;
        if (obj == null)
                return false;
        if (getClass() != obj.getClass())
                return false;
        ObjectMapEditorInput other = (ObjectMapEditorInput) obj;
        if (!id.equals(other.id))
                return false;
        return true;
	}
}
