package org.openmrs.module.cccgenerator.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.cccgenerator.model.CCCCount;
import org.openmrs.module.cccgenerator.model.CCCLocation;
import org.openmrs.module.cccgenerator.service.CCCGeneratorService;
import org.openmrs.web.WebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Controller
public class CCCGeneratorLocationFormController {

	private final String SUCCESS_FORM_VIEW = "module/cccgenerator/cccgeneratorLocationForm.form";

	/**
	 * Logger for this class and subclasses
	 */
	protected final Log log = LogFactory.getLog(getClass());

	@RequestMapping(method = RequestMethod.GET, value = "module/cccgenerator/cccgeneratorLocationForm.form")
	public void whenPageLoads(ModelMap map) {
		CCCGeneratorService service = Context.getService(CCCGeneratorService.class);

		// get all locations from the db
		List<Location> locations = Context.getLocationService().getAllLocations(false);

		// build a list of CCCLocation objects corresponding to locations
		List<CCCLocation> cccLocations = new ArrayList<CCCLocation>();
		for (Location location : locations) {
			CCCLocation cccLocation = service.getCCCLocationByLocation(location);
			if (cccLocation == null) {
				// create a blank un-persisted CCCLocation as a holder
				cccLocation = new CCCLocation();
				cccLocation.setLocation(location);
			}
			cccLocations.add(cccLocation);
		}

		// sort locations alphabetically and add to model
		Collections.sort(cccLocations, new SortLocationsByLocationNameComparator());
		map.addAttribute("locationList", cccLocations);
	}

	@RequestMapping(method = RequestMethod.POST, value = "module/cccgenerator/cccgeneratorLocationForm.form")
	public void processForm(HttpServletRequest request, ModelMap map,
	                        @RequestParam(required = true, value = "location_id") Integer location_id,
	                        @RequestParam(required = true, value = "CCC") Integer ccc
	) {
		CCCGeneratorService service = Context.getService(CCCGeneratorService.class);
		LocationService locService = Context.getLocationService();
		HttpSession httpSession = request.getSession();

		// get the related Location
		Location location = locService.getLocation(location_id);
		if (location == null) {
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "Location " + location_id + " could not be found.");
			return;
		}

		// get the related CCCLocation
		CCCLocation cccLocation = service.getCCCLocationByLocation(location);

		// if it doesn't exist, create a new one
		if (cccLocation == null) {
			cccLocation = new CCCLocation();
			cccLocation.setLocation(location);
		}

		// set the CCC number
		cccLocation.setCCC(ccc);

		// save it
		service.saveCCCLocation(cccLocation);

		// create a new CCCCount
		CCCCount cccCount = new CCCCount();

		// initialize it
		cccCount.setCCC(ccc);
		cccCount.setLastCount(0);
		cccCount.setLocation(cccLocation.getId());

		// save it
		service.saveCCCCount(cccCount);
		httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "Location " + location + " saved with CCC code: " + ccc + ".");

		return;
	}

	private static class SortLocationsByLocationNameComparator implements Comparator<Object> {

		public int compare(Object a, Object b) {
			CCCLocation ae = (CCCLocation) a;
			CCCLocation be = (CCCLocation) b;

			try {
				return ae.getLocation().getName().compareTo(be.getLocation().getName());
			} catch (NullPointerException ex) {
				return 0;
			}
		}
	}
}

