package org.openmrs.module.cccgenerator.db.hibernate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Expression;
import org.openmrs.Cohort;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.cccgenerator.dao.CCCGeneratorDAO;
import org.openmrs.module.cccgenerator.model.CCCCount;
import org.openmrs.module.cccgenerator.model.CCCLocation;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class HibernateCCCGeneratorDAO implements CCCGeneratorDAO {

	private static final Log log = LogFactory.getLog(HibernateCCCGeneratorDAO.class);

	private SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public CCCLocation saveCCCLocation(CCCLocation CCCLocation) {
		sessionFactory.getCurrentSession().saveOrUpdate(CCCLocation);
		return CCCLocation;
	}

	public void saveCCCCount(CCCCount CCCCount) {
		sessionFactory.getCurrentSession().saveOrUpdate(CCCCount);
	}

	public List<CCCLocation> getAllCCCLocations() {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CCCLocation.class);
		return criteria.list();
	}

	public CCCCount getCCCCountByCCC(Integer CCC) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CCCCount.class)
				.add(Expression.eq("CCC", CCC));

		return (CCCCount) criteria.list().get(0);
	}

	public CCCLocation getCCCLocationByLocation(Location location) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CCCLocation.class);
		CCCLocation CCCLocation = (CCCLocation) criteria.add(Expression.eq("location", location)).uniqueResult();
		return CCCLocation;
	}

	public List<CCCCount> getAllRelatedSites(Integer CCC) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CCCCount.class)
				.add(Expression.eq("CCC", CCC));

		return criteria.list();
	}

	public Set<Integer> getIdsOfLocationsParentAndChildren(Integer locId) {

		Location location = Context.getLocationService().getLocation(locId);
		Location parentLocation = null;
		Set<Location> allChildrenLocation = new HashSet<Location>();
		Set<Integer> allChildrenLoactionAndParentIds = new HashSet<Integer>();
		String intToString = null;

		if (location.getParentLocation() != null) {

			allChildrenLocation.add(location.getParentLocation());
			allChildrenLocation.addAll(location.getParentLocation().getChildLocations(false));
		} else if (location.getChildLocations() != null) {
			allChildrenLocation.add(location);
			allChildrenLocation.addAll(location.getChildLocations(false));

		} else {
			//otherwise just pick the location
			allChildrenLocation.add(location);

		}
		for (Location l : allChildrenLocation) {
			//ids for all the loctions got at this level
			allChildrenLoactionAndParentIds.add(l.getLocationId());
		}

		return allChildrenLoactionAndParentIds;
	}

	public Cohort excludeDiscontinued() {
		Set<Integer> disc = new HashSet<Integer>();
		Query discoList = sessionFactory.getCurrentSession().createSQLQuery(
				" SELECT person_id FROM obs "
						+ " WHERE "
						+ " (concept_id=1946"
						+ " and "
						+ " value_coded=1065)"
						+ " or "
						+ " (concept_id=1596"
						+ " and "
						+ " value_coded=1946 )"
		);
		disc.addAll(discoList.list());
		return new Cohort("Patient to discontinue ", "", disc);
	}

	public Map<Location, Set<Integer>> getLocationPatientMap() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}


}
