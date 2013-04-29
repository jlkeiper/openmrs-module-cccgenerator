package org.openmrs.module.cccgenerator.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Location;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.cccgenerator.dao.CCCGeneratorDAO;
import org.openmrs.module.cccgenerator.model.CCCCount;
import org.openmrs.module.cccgenerator.model.CCCLocation;
import org.openmrs.module.cccgenerator.service.CCCGeneratorService;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class CCCGeneratorServiceImpl extends BaseOpenmrsService implements CCCGeneratorService {

	private static Log log = LogFactory.getLog(CCCGeneratorServiceImpl.class);

	private CCCGeneratorDAO dao;

	public void setDao(CCCGeneratorDAO dao) {
		this.dao = dao;
	}

	public List<CCCLocation> getAllCCCLocations() {
		return dao.getAllCCCLocations();
	}

	public CCCLocation saveCCCLocation(CCCLocation CCCLocation) {
		return dao.saveCCCLocation(CCCLocation);
	}

	public CCCCount getCCCCountByCCC(Integer CCC) {
		return dao.getCCCCountByCCC(CCC);
	}

	public void saveCCCCount(CCCCount CCCCount) {
		dao.saveCCCCount(CCCCount);
	}

	public CCCLocation getCCCLocationByLocation(Location location) {
		return dao.getCCCLocationByLocation(location);
	}

	public List<CCCCount> getAllRelatedSites(Integer CCC) {
		return dao.getAllRelatedSites(CCC);
	}

	public Set<Integer> getIdsOfLocationsParentAndChildren(Integer locId) {
		return dao.getIdsOfLocationsParentAndChildren(locId);
	}

	public Cohort excludeDiscontinued() {
		return dao.excludeDiscontinued();
	}

	public Map<Location, Set<Integer>> getLocationPatientMap() {
		return dao.getLocationPatientMap();
	}

}
