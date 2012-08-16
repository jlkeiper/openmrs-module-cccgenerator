package org.openmrs.module.cccgenerator.service;

import org.openmrs.*;
import org.openmrs.api.OpenmrsService;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.cccgenerator.model.CCCCount;
import org.openmrs.module.cccgenerator.model.CCCLocation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Transactional
public interface CCCGeneratorService extends OpenmrsService {

 @Transactional(readOnly = true)
public List<CCCLocation> getAllCCCLocations();

@Transactional(readOnly = true)
public List<CCCLocation> getCCCLocationById(int id);

public CCCLocation saveCCCLocation(CCCLocation CCClocation);

@Transactional(readOnly = true)
public List<CCCCount> getAllCCCCount();

@Transactional(readOnly = true)
public CCCCount getCCCCountByCCC(Integer CCC);

@Transactional(readOnly = true)
public CCCCount getCCCCountById(int id);

public void saveCCCCount(CCCCount CCCcount);

@Transactional(readOnly = true)
public List<Patient> getPatient();

@Transactional(readOnly = true)
public CCCLocation getCCCLocationByLocation(Location location);

@Transactional(readOnly = true)
public PatientIdentifier getPatientByPidItypeLocation(Patient p,PatientIdentifierType i);

@Transactional(readOnly = true)
public Cohort getAllHIVPatients() throws DAOException;

@Transactional(readOnly = true)
public Cohort getPatientByIdentifierAndLocation(Integer identifierId,Integer locationId) throws DAOException;

@Transactional(readOnly = true)
public  List<CCCCount>  getAllRelatedSites(Integer CCC);

@Transactional(readOnly = true)
public Set<Integer> getIdsOfLocationsParentAndChildren(Integer locId);

@Transactional(readOnly = true)
public Cohort excludeDiscontinued();
}
