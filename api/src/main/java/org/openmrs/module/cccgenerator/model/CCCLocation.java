package org.openmrs.module.cccgenerator.model;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.Location;

public class CCCLocation extends BaseOpenmrsData {

	Integer id;
	Location location;
	Integer CCC;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getCCC() {
		return CCC;
	}

	public void setCCC(Integer CCC) {
		this.CCC = CCC;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}
}



