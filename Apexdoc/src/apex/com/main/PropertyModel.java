package apex.com.main;

import java.util.ArrayList;

public class PropertyModel extends ApexModel {

	public PropertyModel() {
		super();
	}
	
	public PropertyModel(String name, ArrayList<String> comments){
		this.setNameLine(name);
		this.parseComments(comments);
	}
	
	

	public void setNameLine(String nameLine) {
		if (nameLine != null) {
			// remove any trailing stuff after property name. { =
			int i = nameLine.indexOf('{');
			if (i == -1) i = nameLine.indexOf('=');
			if (i == -1) i = nameLine.indexOf(';');
			if (i >= 0) nameLine = nameLine.substring(0, i);		    	

		}
		super.setNameLine(nameLine);
	}

	public String getPropertyName(){
		String nameLine = getNameLine().trim();
		if(nameLine != null && nameLine.length() > 0 ){
			int lastindex = nameLine.lastIndexOf(" ");
			if (lastindex >= 0) { 
				String propertyName = nameLine.substring(lastindex + 1);
				return propertyName;
			}
		}
		return "";
	}
}
