package org.openmrs.module.cccgenerator.service.impl;

import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.DAOException;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.cccgenerator.dao.CCCGeneratorDAO;
import org.openmrs.module.cccgenerator.model.CCCCount;
import org.openmrs.module.cccgenerator.model.CCCLocation;
import org.openmrs.module.cccgenerator.service.CCCGeneratorService;

public class CCCGeneratorServiceImpl extends BaseOpenmrsService implements CCCGeneratorService {
	
	private static Log log = LogFactory.getLog(CCCGeneratorServiceImpl.class);
	
	private CCCGeneratorDAO dao;
	
	public void setDao(CCCGeneratorDAO dao) {
		this.dao = dao;
	}
	
	public static final String ENCOUNTER_TYPE_ADULT_RETURN = "ADULTRETURN";
	public static final String ENCOUNTER_TYPE_ADULT_INITIAL = "ADULTINITIAL";
	public static final String ENCOUNTER_TYPE_PEDS_INITIAL = "PEDSINITIAL";
	public static final String ENCOUNTER_TYPE_PEDS_RETURN = "PEDSRETURN";
	
	
	public List<CCCLocation> getAllCCCLocations() {
	
		return dao.getAllCCCLocations();
	}

	public List<CCCLocation> getCCCLocationById(int id) {
		
		return dao.getCCCLocationById(id);
	}


	public CCCLocation saveCCCLocation(CCCLocation CCCLocation) {
	
		return	dao.saveCCCLocation(CCCLocation);
		
	}

	
	public List<CCCCount> getAllCCCCount() {
		return dao.getAllCCCCount();
	}

	public CCCCount getCCCCountByCCC(Integer CCC) {
	
		return dao.getCCCCountByCCC(CCC);
	}

	public CCCCount getCCCCountById(int id) {
			return dao.getCCCCountById(id);
	}

	public void saveCCCCount(CCCCount CCCCount) {
		 dao.saveCCCCount(CCCCount);
		
	}

	

	public List<Patient> getPatient() {
		EncounterService encservice = Context.getEncounterService();
		PatientService patservice = Context.getPatientService();
		List<Patient> listOfHIVPositivePatients=new ArrayList<Patient>();
		
		
		List<Patient> allPatient=patservice.getAllPatients();
		//log.info("\n\n\n"+allPatient.size()+"all patients");
		for(Patient pat:allPatient){
			//log.info(pat.getNames());
			List<Encounter> listOfEncounters=encservice.getEncountersByPatientId(pat.getPatientId());
			//log.info(listOfEncounters.size());
			//boolean found=false;
			Collections.sort(listOfEncounters, new SortEncountersByDateComparator());
			//log.info(listOfEncounters.size());
				for(Encounter encs:listOfEncounters){
					
					//log.info("\n\n Here is the encounter "+encs.getEncounterType().getName());
					
					if(encs.getEncounterType().getName().equals(ENCOUNTER_TYPE_ADULT_INITIAL)
							||encs.getEncounterType().getName().equals(ENCOUNTER_TYPE_ADULT_RETURN)
							||encs.getEncounterType().getName().equals(ENCOUNTER_TYPE_PEDS_INITIAL)
							||encs.getEncounterType().getName().equals(ENCOUNTER_TYPE_PEDS_RETURN)){
						
						listOfHIVPositivePatients.add(pat);
						//found=true;
						break;
					}
				}
				
			
		}
		log.info("\n\n Here is the length "+listOfHIVPositivePatients.size());
		
		return listOfHIVPositivePatients;
	}

	public Patient getPatientById(int id) {
		// TODO Auto-generated method stub
		return null;
	}
		
	

	private static class SortEncountersByDateComparator implements Comparator<Object> {

		//@Override
		public int compare(Object a, Object b) {
			Encounter ae = (Encounter) a;
			Encounter be = (Encounter) b;
			return ae.getEncounterDatetime().compareTo(be.getEncounterDatetime());
		}
	}



	public CCCLocation getCCCLocationByLocation(Location location) {
		return dao.getCCCLocationByLocation(location);
	}

	public PatientIdentifier getPatientByPidItypeLocation(Patient p,
			PatientIdentifierType i) {
		
		return dao.getPatientByPidItypeLocation(p,i);
	}
	public Cohort getAllHIVPatients() throws DAOException {
		return dao.getAllHIVPatients();
	}

    public Cohort getPatientByIdentifierAndLocation(Integer identifierId,Integer locationId) throws DAOException{
        return dao.getPatientByIdentifierAndLocation(identifierId,locationId);
    }

    public  List<CCCCount>  getAllRelatedSites(Integer CCC){
       return dao.getAllRelatedSites(CCC);
    }
    public Set<Integer> getIdsOfLocationsParentAndChildren(Integer locId){
        return dao.getIdsOfLocationsParentAndChildren(locId);
    }

    public Cohort excludeDiscontinued(){
        return dao.excludeDiscontinued();
    }

}
