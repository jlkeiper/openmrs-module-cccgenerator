package org.openmrs.module.cccgenerator.dao;

import java.util.List;
import java.util.Set;

import org.openmrs.Cohort;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.module.cccgenerator.model.CCCCount;
import org.openmrs.module.cccgenerator.model.CCCLocation;

public interface CCCGeneratorDAO {

	public List<CCCLocation> getCCCLocationById(Integer id);

	public List<CCCLocation> getAllCCCLocations();

	public CCCLocation saveCCCLocation(CCCLocation CCCLocation);
	
	
	public CCCCount getCCCCountById(Integer id);

	public List<CCCCount> getAllCCCCount();

	public void saveCCCCount(CCCCount CCCCount);
	
	
	public CCCLocation getCCCLocationByName(CCCLocation CCCLocation);
	
	public CCCCount getCCCCountByCCC(Integer CCC);
	
	public CCCLocation getCCCLocationByLocation(Location location);
	
	public PatientIdentifier getPatientByPidItypeLocation(Patient p,PatientIdentifierType i);
	
	public Cohort getAllHIVPatients();

    public Cohort getPatientByIdentifierAndLocation(Integer identifierId,Integer locationId);

    public  List<CCCCount> getAllRelatedSites(Integer CCC);

    public Set<Integer> getIdsOfLocationsParentAndChildren(Integer locId);

    public Cohort excludeDiscontinued();
	
}
