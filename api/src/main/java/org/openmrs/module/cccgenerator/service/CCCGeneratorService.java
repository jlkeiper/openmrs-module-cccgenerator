package org.openmrs.module.cccgenerator.service;

import org.openmrs.*;
import org.openmrs.api.OpenmrsService;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.cccgenerator.model.CCCCount;
import org.openmrs.module.cccgenerator.model.CCCLocation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Transactional
public interface CCCGeneratorService extends OpenmrsService {

	@Transactional(readOnly = true)
	public List<CCCLocation> getAllCCCLocations();

	@Transactional
	public CCCLocation saveCCCLocation(CCCLocation CCClocation);

	@Transactional(readOnly = true)
	public CCCCount getCCCCountByCCC(Integer CCC);

	@Transactional
	public void saveCCCCount(CCCCount CCCcount);

	@Transactional(readOnly = true)
	public CCCLocation getCCCLocationByLocation(Location location);

	@Transactional(readOnly = true)
	public List<CCCCount> getAllRelatedSites(Integer CCC);

	@Transactional(readOnly = true)
	public Set<Integer> getIdsOfLocationsParentAndChildren(Integer locId);

	@Transactional(readOnly = true)
	public Cohort excludeDiscontinued();

	@Transactional(readOnly = true)
	public Map<Location, Set<Integer>> getLocationPatientMap();
}
