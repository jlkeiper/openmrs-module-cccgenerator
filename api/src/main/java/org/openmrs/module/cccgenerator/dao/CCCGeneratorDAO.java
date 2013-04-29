package org.openmrs.module.cccgenerator.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openmrs.Cohort;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.module.cccgenerator.model.CCCCount;
import org.openmrs.module.cccgenerator.model.CCCLocation;

public interface CCCGeneratorDAO {

	public List<CCCLocation> getAllCCCLocations();

	public CCCLocation saveCCCLocation(CCCLocation CCCLocation);

	public void saveCCCCount(CCCCount CCCCount);
	
	public CCCCount getCCCCountByCCC(Integer CCC);
	
	public CCCLocation getCCCLocationByLocation(Location location);
	
    public  List<CCCCount> getAllRelatedSites(Integer CCC);

    public Set<Integer> getIdsOfLocationsParentAndChildren(Integer locId);

    public Cohort excludeDiscontinued();

	public Map<Location,Set<Integer>> getLocationPatientMap();
}
