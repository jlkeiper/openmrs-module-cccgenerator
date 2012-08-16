package org.openmrs.module.cccgenerator.web.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.cccgenerator.model.CCCCount;
import org.openmrs.module.cccgenerator.model.CCCLocation;
import org.openmrs.module.cccgenerator.service.CCCGeneratorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
@Controller

	
	
	public class CCCGeneratorLocationFormController {
	private final String SUCCESS_FORM_VIEW="module/cccgenerator/cccgeneratorForm.form";
		
		/** Logger for this class and subclasses */
		protected final Log log = LogFactory.getLog(getClass());
		
		@RequestMapping(method=RequestMethod.GET,value = "module/cccgenerator/cccgeneratorLocationForm.form")
        public void whenPageLoads(ModelMap map) {
            CCCGeneratorService service=Context.getService(CCCGeneratorService.class);
			
			List<CCCLocation> cccLocation=service.getAllCCCLocations();
            
			List<Location> location=new ArrayList<Location>(); 
            //CCCLocation=service.getAllCCCLocations();    
            
            
            for(CCCLocation mlocation:cccLocation){
            	location.add(Context.getLocationService().getLocation(mlocation.getLocation().getLocationId()));
            	
            }
            	
            	
			List<Location> locationsList = Context.getLocationService().getAllLocations();
			locationsList.removeAll(location);

            Collections.sort(locationsList, new SortLocationsByLocationIdComparator());    
                //map.addAttribute("location", location);
                
                map.addAttribute("locationList", locationsList);
                
        }
		
		
		@RequestMapping(method=RequestMethod.POST,value = "module/cccgenerator/cccgeneratorLocationForm.form")
         public void processForm(ModelMap map,
        		 @RequestParam(required=true, value="location_id") String location_id,
        		 @RequestParam(required=true, value="CCC") String ccc
        		 ) {

			 LocationService locService=Context.getLocationService();
			 
			 
			 
			 CCCGeneratorService service=Context.getService(CCCGeneratorService.class);
			 
			 CCCLocation cccLocation1= new CCCLocation();
			 CCCCount cccCount=new CCCCount();


			 cccLocation1.setLocation(locService.getLocation(Integer.parseInt(location_id)));
			 cccLocation1.setCCC(Integer.parseInt(ccc));

			 cccCount.setCCC(Integer.parseInt(ccc));
			 cccCount.setLastCount(0);
             cccCount.setLocation(Integer.parseInt(location_id));



			 service.saveCCCLocation(cccLocation1);
			 service.saveCCCCount(cccCount);
             //service.saveAllRelatedSites(0,CCC);
			
			 List<CCCLocation> CCCLocation=service.getAllCCCLocations();
	            
				List<Location> location=new ArrayList<Location>(); 
	            //CCCLocation=service.getAllCCCLocations();


	            for(CCCLocation mlocation:CCCLocation){
	            	location.add(Context.getLocationService().getLocation(mlocation.getLocation().getLocationId()));
	            	
	            }
	            	
	            	
				List<Location> locationsList = Context.getLocationService().getAllLocations();
				locationsList.removeAll(location);
	            Collections.sort(locationsList, new SortLocationsByLocationIdComparator());    
	                //map.addAttribute("location", location);
	                
	                map.addAttribute("locationList", locationsList);
			 //return "redirect:CCCgeneratorForm.form?CCCId="+CCCLocation.getCCC();
		 }
		 
	private static class SortLocationsByLocationIdComparator implements Comparator<Object>{

		public int compare(Object a, Object b) {
			Location ae=(Location) a; 
			Location be=(Location) b;
		
			
			return ae.getLocationId().compareTo(be.getLocationId());
		}
		
	}



}

