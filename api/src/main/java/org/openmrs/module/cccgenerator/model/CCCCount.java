package org.openmrs.module.cccgenerator.model;

import org.openmrs.BaseOpenmrsData;

public class CCCCount extends BaseOpenmrsData {

	private Integer id;
	private Integer CCC;
	private Integer lastCount;
	private Integer location;

	public Integer getLocation() {
		return location;
	}

	public void setLocation(Integer location) {
		this.location = location;
	}

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

	public Integer getLastCount() {
		return lastCount;
	}

	public void setLastCount(Integer lastCount) {
		this.lastCount = lastCount;
	}
}
