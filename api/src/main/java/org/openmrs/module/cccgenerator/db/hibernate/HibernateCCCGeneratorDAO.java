package org.openmrs.module.cccgenerator.db.hibernate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Expression;
import org.openmrs.*;
import org.openmrs.api.context.Context;
import org.openmrs.module.cccgenerator.dao.CCCGeneratorDAO;
import org.openmrs.module.cccgenerator.model.CCCCount;
import org.openmrs.module.cccgenerator.model.CCCLocation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class HibernateCCCGeneratorDAO implements CCCGeneratorDAO {
	


	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(HibernateCCCGeneratorDAO.class);
	
	private SessionFactory sessionFactory;
	
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	
	
	public List<CCCLocation> getCCCLocation(List<CCCLocation> location) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Location.class)
        .add(Expression.eq("voided", false));

		return criteria.list();
		
	}

	public CCCLocation saveCCCLocation(CCCLocation CCCLocation) {
			sessionFactory.getCurrentSession().saveOrUpdate(CCCLocation);
			return CCCLocation; 
		
	}

	public List<CCCLocation> getCCCLocationsById(int Id) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Location.class)
        .add(Expression.eq("voided", false)).add(Expression.eq("CCC_location_id", Id));
		
		return criteria.list();
	}

	public List<CCCLocation> getCCCLocationByStatus(int status) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CCCLocation.class)
        .add(Expression.eq("voided", status));
		
		return criteria.list();
	}

	public CCCLocation getCCCLocationById(int id) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Location.class)
        .add(Expression.eq("voided", false)).add(Expression.eq("CCC_location_id", id));
		
		List<CCCLocation> CCCLocation = criteria.list();
		if (null == CCCLocation || CCCLocation.isEmpty()) {
			return null;
		}
		return CCCLocation.get(0);
	}

	public void updateCCCLocation(CCCLocation CCCLocation) {
		sessionFactory.getCurrentSession().update(CCCLocation);

	}

	public List<CCCCount> getCCCCount() {
		// TODO Auto-generated method stub
		return null;
	}

	public void saveCCCCount(CCCCount CCCCount) {
		sessionFactory.getCurrentSession().saveOrUpdate(CCCCount);

		
	}

	public List<CCCCount> getCCCCountsById(int Id) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CCCCount.class)
        .add(Expression.eq("voided", false)).add(Expression.eq("CCC_location_id", Id));
		
		return criteria.list();
	}

	public List<CCCCount> getCCCCountByStatus(int status) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CCCCount.class)
        .add(Expression.eq("voided", status));
		
		return criteria.list();
	}

	public CCCCount getCCCCountById(int id) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CCCCount.class)
        .add(Expression.eq("voided", false)).add(Expression.eq("CCC_count_id", id));
		
		List<CCCCount> CCCCount = criteria.list();
		if (null == CCCCount || CCCCount.isEmpty()) {
			return null;
		}
		return CCCCount.get(0);
	}

	public void updateCCCCount(CCCCount CCCCount) {
		sessionFactory.getCurrentSession().update(CCCCount);
		
	}

	public List<Location> getLocation(List<Location> location) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Location.class)
        .add(Expression.eq("voided", false));
		
		return criteria.list();
	}

	public List<Location> getLocationsById(int Id) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Location.class)
        .add(Expression.eq("voided", false)).add(Expression.eq("loation_id", Id));
		
		return criteria.list();
	}

	public List<Location> getLocationByStatus(int status) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Location.class)
        .add(Expression.eq("voided", status));
		
		return criteria.list();
	}

	public Location getLocationById(int id) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Location.class)
        .add(Expression.eq("voided", false)).add(Expression.eq("location_id", id));
		
		List<Location> location = criteria.list();
		if (null == location || location.isEmpty()) {
			return null;
		}
		return location.get(0);
	}

	public List<Patient> getPatient() {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Location.class)
        .add(Expression.eq("voided", false));
		
		return criteria.list();
	}

	public List<Patient> getPatientsById(int Id) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Patient.class)
        .add(Expression.eq("voided", false)).add(Expression.eq("patient_id", Id));
		
		return criteria.list();
	}

	public List<Patient> getPatientByStatus(int status) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Patient.class)
        .add(Expression.eq("voided", status));
		
		return criteria.list();
	}

	public Patient getPatientById(int id) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Patient.class)
        .add(Expression.eq("voided", false)).add(Expression.eq("patient_id", id));
		
		List<Patient> patient = criteria.list();
		if (null == patient || patient.isEmpty()) {
			return null;
		}
		return patient.get(0);
	}

	public List<CCCLocation> getCCCLocationById(Integer id) {
		Criteria criteria =sessionFactory.getCurrentSession().createCriteria(CCCLocation.class)
		.add(Expression.eq("voided",false)).add(Expression.eq("CCC_location_id",id));
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<CCCLocation> getAllCCCLocations() {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CCCLocation.class);
		return criteria.list();
	}

	public void deleteCCCLocation(CCCLocation CCCLcoation) {
		// TODO Auto-generated method stub
		
	}

	public CCCCount getCCCCountById(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<CCCCount> getAllCCCCount() {
		// TODO Auto-generated method stub
		return null;
	}

	public CCCCount deleteCCCCount(CCCCount CCCCount) {
		// TODO Auto-generated method stub
		return null;
	}

	public CCCLocation getCCCLocationByName(CCCLocation CCCLocation) {
		// TODO Auto-generated method stub
		return null;
	}

	public CCCCount getCCCCountByCCC(Integer CCC) {
		Criteria criteria =sessionFactory.getCurrentSession().createCriteria(CCCCount.class)
		.add(Expression.eq("CCC", CCC));

		return (CCCCount) criteria.list().get(0);
	}

	public CCCLocation getCCCLocationByLocation(Location location) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CCCLocation.class);
		CCCLocation CCCLocation = (CCCLocation) criteria.add(Expression.eq("location", location)).uniqueResult();
		return CCCLocation;
	}

	public PatientIdentifier getPatientByPidItypeLocation(Patient p,
			PatientIdentifierType i) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PatientIdentifier.class);
		PatientIdentifier patientIdentifier=(PatientIdentifier) criteria
		.add(Expression.eq("patient",p))
		.add(Expression.eq("identifierType",i))
		.uniqueResult();
		return patientIdentifier;
	}
	@SuppressWarnings("unchecked")
	public Cohort getAllHIVPatients() {

        String glbccc=Context.getAdministrationService().getGlobalProperty("cccgenerator.CCC");
        PatientIdentifierType patientIdentifier=Context.getPatientService().getPatientIdentifierTypeByName(glbccc) ;
		

        Set<Integer> patientAlreadyDone=new HashSet<Integer>();
        Query queryAllPatients = sessionFactory.getCurrentSession().createSQLQuery
                (
                " SELECT (pident.patient_id) "
                +" FROM patient_identifier pident "
                +" WHERE "
                +" pident.identifier_type='"+patientIdentifier.getPatientIdentifierTypeId()+"'"
               );
        patientAlreadyDone.addAll(queryAllPatients.list());
		return new Cohort("All Hiv postive patients", "", patientAlreadyDone);
	}

    public Cohort getPatientByIdentifierAndLocation(Integer identifierId,Integer locationId){

        Location location=Context.getLocationService().getLocation(locationId);
        Location parentLocation=null;
        Set<Location> allChildrenLocation=new HashSet<Location>();
        Set<Integer> allChildrenLoactionAndParentIds=new HashSet<Integer>();
        String intToString=null;

                 if(location.getParentLocation() !=null){

                     allChildrenLocation.add(location.getParentLocation());
                     allChildrenLocation.addAll(location.getParentLocation().getChildLocations(false));
                 }
                 else if(location.getChildLocations() !=null){
                     allChildrenLocation.add(location);
                     allChildrenLocation.addAll(location.getChildLocations(false));

                 }
                else  {
                    //otherwise just pick the location
                     allChildrenLocation.add(location);

                 }



                for(Location l:allChildrenLocation){
                    //ids for all the loctions got at this level
                    allChildrenLoactionAndParentIds.add(l.getLocationId());
                }

                 Set<Integer>  allIds=new HashSet<Integer>();

                for(Integer i:allChildrenLoactionAndParentIds){
                    Query queryAllWithIdentifierAndLocation= sessionFactory.getCurrentSession().createSQLQuery
                            (
                                    " SELECT (pident.patient_id) "
                                    +" FROM patient_identifier pident "
                                    +" WHERE "
                                    +" pident.identifier_type= '"+identifierId+"'"
                                    +" and "
                                    +" pident.location_id = '"+i +"'"
                            );
                    allIds.addAll(queryAllWithIdentifierAndLocation.list());
                }
                 //log.info("This is what comes out "+allIds.size());
        return  new Cohort("","",allIds);


    }

    public  List<CCCCount>  getAllRelatedSites(Integer CCC){
        Criteria criteria =sessionFactory.getCurrentSession().createCriteria(CCCCount.class)
                .add(Expression.eq("CCC",CCC));

        return criteria.list();
    }

    public Set<Integer> getIdsOfLocationsParentAndChildren(Integer locId){

        Location location=Context.getLocationService().getLocation(locId);
        Location parentLocation=null;
        Set<Location> allChildrenLocation=new HashSet<Location>();
        Set<Integer> allChildrenLoactionAndParentIds=new HashSet<Integer>();
        String intToString=null;

        if(location.getParentLocation() !=null){

            allChildrenLocation.add(location.getParentLocation());
            allChildrenLocation.addAll(location.getParentLocation().getChildLocations(false));
        }
        else if(location.getChildLocations() !=null){
            allChildrenLocation.add(location);
            allChildrenLocation.addAll(location.getChildLocations(false));

        }
        else  {
            //otherwise just pick the location
            allChildrenLocation.add(location);

        }
        for(Location l:allChildrenLocation){
            //ids for all the loctions got at this level
            allChildrenLoactionAndParentIds.add(l.getLocationId());
        }

        return allChildrenLoactionAndParentIds;
    }

    public Cohort excludeDiscontinued(){
        Set<Integer> disc=new HashSet<Integer>();
             Query discoList=sessionFactory.getCurrentSession().createSQLQuery(
                " SELECT person_id FROM obs "
                 + " WHERE "
                 + " (concept_id=1946"
                 + " and "
                 + " value_coded=1065)"
                 + " or "
                 + " (concept_id=1596"
                 + " and "
                 + " value_coded=1946 )"
             ) ;
            disc.addAll(discoList.list());
        return  new Cohort("Patient to discontinue ","", disc);
    }


}
