/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.cccgenerator.web.controller;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.*;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.cccgenerator.model.CCCCount;
import org.openmrs.module.cccgenerator.model.CCCLocation;
import org.openmrs.module.cccgenerator.service.CCCGeneratorService;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.*;


@Controller
public class CCCGeneratorFormController {

	private final Log log = LogFactory.getLog(getClass());
	
	public static final String  ENCOUNTER_TYPE_ADULT_RETURN = "ADULTRETURN";
	public static final String  ENCOUNTER_TYPE_ADULT_INITIAL = "ADULTINITIAL";
	public static final String  ENCOUNTER_TYPE_PEDS_INITIAL = "PEDSINITIAL";
	public static final String  ENCOUNTER_TYPE_PEDS_RETURN = "PEDSRETURN";
    public static  final String ENCOUNTER_TYPE_BASELINE_INVESTIGATION="BASELINEINVESTIGATION";
    public static  final String ENCOUNTER_TYPE_PMTCTANC="PMTCTANC";
    public static  final String ENCOUNTER_TYPE_PMTCTPOSTNATAL ="PMTCTPOSTNATAL ";
    public static  final String ENCOUNTER_TYPE_ECPeds ="ECPeds";
    public static  final String ENCOUNTER_TYPE_ECSTABLE ="ECSTABLE";

	
	@RequestMapping(method=RequestMethod.GET, value = "module/cccgenerator/cccgeneratorForm.form")
    public void whenPageLoads(ModelMap map){

        LocationService locationService=Context.getLocationService();
        PatientService patientService=Context.getPatientService();



        List<Location> siteLocation=locationService.getAllLocations(false);
        List<CCCLocation> listofLocationWithCCC=Context.getService(CCCGeneratorService.class).getAllCCCLocations();
        List<Location> uniqueLocation=new ArrayList<Location>();

        for(CCCLocation listofLocationWithCCC1:listofLocationWithCCC){
             //find all the locations that have a supplied CCC
            uniqueLocation.add(locationService.getLocation(listofLocationWithCCC1.getLocation().getLocationId()));

        }


       Set<Location> locationsToIncludeOnlyParents=new HashSet<Location>();

        for(Location location_parent:uniqueLocation){


            if(location_parent.getParentLocation() !=null) {

                locationsToIncludeOnlyParents.add(location_parent.getParentLocation());
            }
            else if(location_parent.getChildLocations(false) !=null){

                locationsToIncludeOnlyParents.add(location_parent);
            }
            else{

                locationsToIncludeOnlyParents.add(location_parent);
            }

        }


        CohortDefinitionService cohortDefinitionService=Context.getService(CohortDefinitionService.class);
        List<CohortDefinition> listOfCohorts= cohortDefinitionService.getAllDefinitions(false);

        map.addAttribute("listOfCohort",listOfCohorts);
        map.addAttribute("siteLocations",locationsToIncludeOnlyParents);

    }

    @RequestMapping(method=RequestMethod.POST, value = "module/cccgenerator/cccgeneratorForm.form")
    public void whenPageIsPosted(ModelMap map,HttpServletRequest request,
                                 @RequestParam(required=false, value="site") String siteId,
                                 @RequestParam(required=true, value="cohort") String cohortdefuuid
                                    ){
        //lengthen the session to make sure generation is complete
        HttpSession httpSession=request.getSession();
        Integer httpSessionvalue=httpSession.getMaxInactiveInterval();
        httpSession.setMaxInactiveInterval(-1);



        CCCGeneratorService service=Context.getService(CCCGeneratorService.class);
        EncounterService encounterservice=Context.getEncounterService();
        AdministrationService adminservice=Context.getAdministrationService();
        PatientService pService=Context.getPatientService();
        LocationService locservice=Context.getLocationService();
        List<Patient> listOfHIVPatientsPerSite=new ArrayList<Patient>();
        Set<Integer> patientIdsFromCohort=null;
        ///////try to find all the required ids for patient

        CohortDefinitionService cohortDefinitionService=Context.getService(CohortDefinitionService.class);
        List<CohortDefinition> listOfCohorts= cohortDefinitionService.getAllDefinitions(false);

        List<Location> listOfLocations=Context.getLocationService().getAllLocations(false);

        map.addAttribute("listOfCohort",listOfCohorts);


        CohortDefinition cohortDefinition= Context.getService(CohortDefinitionService.class).getDefinitionByUuid(cohortdefuuid);
        try{
        EvaluationContext evaluationContext = new EvaluationContext();

        //add loctation to be displayed here
        ///evaluationContext.addParameterValue("locationList",Arrays.asList(Context.getLocationService().getLocation(Integer.parseInt(siteId))));


        //evaluation
        Cohort cohort = cohortDefinitionService.evaluate(cohortDefinition, evaluationContext);
            patientIdsFromCohort=new HashSet<Integer>();
            patientIdsFromCohort.addAll(cohort.getMemberIds());

        }
        catch (Exception e){
            e.printStackTrace();
        }
        //////////////////////////////////////////////////

         //get the location from the jsp interface
        Location siteLocation=locservice.getLocation(Integer.parseInt(siteId));

        map.addAttribute("location1",siteLocation.getName());
        Integer CCC=0;
        Integer lastcount=0;
        String CCCIdentifier="";
        String glbCCC="";
        int number_of_hiv_patients_affected = 0;
        int number_of_hiv_patients_affected_generated = 0;

        //cohort to return all patient ids




        log.info("Get all patients who are  HIV positive "+patientIdsFromCohort.size());

        //get cohort of all the patient ids in the database
        Cohort allpatientscohort=Context.getPatientSetService().getAllPatients();

        Set<Integer>  allpatientscohortset=allpatientscohort.getMemberIds();

            log.info("All patients in the databse are "+allpatientscohortset.size());

        //get all patients to exclude from the set
        Set<Integer> toExclude=new HashSet<Integer>(Context.getService(CCCGeneratorService.class).excludeDiscontinued().getMemberIds());
        log.info("The number of patients discontinued from care "+toExclude.size());

        //remove the above from the patientIdsFromCohort to get only true matches

        patientIdsFromCohort.removeAll(toExclude);
        //get the intersection of only the patients who are present no empty slots

        Set<Integer> uniqueSetids=new HashSet<Integer>(CollectionUtils.intersection(allpatientscohortset,patientIdsFromCohort));

            log.info("Only unique numbers selected "+uniqueSetids.size());

        //get aready generated members
        Cohort listOfHIVPatientIdAlreadygenerated=service.getAllHIVPatients();
        Set<Integer> listOfHIVPatientIdAlreadygeneratedpatientsIds=new HashSet<Integer>(listOfHIVPatientIdAlreadygenerated.getMemberIds());

           log.info("Already generated ones are "+listOfHIVPatientIdAlreadygeneratedpatientsIds.size());
        
        //get the difference in number of HIV patients

          uniqueSetids.removeAll(listOfHIVPatientIdAlreadygeneratedpatientsIds);


         log.info("Difference comes in here excluding already generated ones "+uniqueSetids.size());


		Patient patients;

        int count=0;

		for(Integer patientId:uniqueSetids){
              count++;
			  log.info("This patient Number "+patientId+" and this number "+count+" Remaining "+(uniqueSetids.size()-count));
              cleanupafterCount(count);
			patients=pService.getPatient(patientId);
			
			List<Encounter> listOfEncounters=encounterservice.getEncountersByPatientId(patientId);
			
			Collections.sort(listOfEncounters, new SortEncountersByDateComparator());
			
			   
			
			for(Encounter encounters:listOfEncounters){

                        log.info("Got in the encounters loop");
								
					if(encounters.getEncounterType().getName().equals(ENCOUNTER_TYPE_ADULT_INITIAL)
							||encounters.getEncounterType().getName().equals(ENCOUNTER_TYPE_ADULT_RETURN)
							||encounters.getEncounterType().getName().equals(ENCOUNTER_TYPE_PEDS_INITIAL)
							||encounters.getEncounterType().getName().equals(ENCOUNTER_TYPE_PEDS_RETURN)
                            ||encounters.getEncounterType().getName().equals(ENCOUNTER_TYPE_BASELINE_INVESTIGATION)
                            ||encounters.getEncounterType().getName().equals(ENCOUNTER_TYPE_PMTCTANC)
                            ||encounters.getEncounterType().getName().equals(ENCOUNTER_TYPE_PMTCTPOSTNATAL)
                            ||encounters.getEncounterType().getName().equals(ENCOUNTER_TYPE_ECPeds)
                            ||encounters.getEncounterType().getName().equals(ENCOUNTER_TYPE_ECSTABLE)
                            ){

                                                         // log.info("Found a patient having HIV encounters");
                                            // log.info("Locations are seen here if equal is when we enter the loop ");
                                             log.info(encounters.getLocation()+"=="+locservice.getLocation(Integer.parseInt(siteId)));
                                       //starts here
                                       //get all the related locations in the database

                                   Set<Integer> getAllLocations=Context.getService(CCCGeneratorService.class).getIdsOfLocationsParentAndChildren(Integer.parseInt(siteId));
                                           log.info("All locations and their sub sites "+getAllLocations.size());
                        for(Integer i:getAllLocations){
                                    if(encounters.getLocation()==locservice.getLocation(i)){

                                        log.info("Checking for provided location from the encounters");

										CCCLocation ml=service.getCCCLocationByLocation(locservice.getLocation(Integer.parseInt(siteId)));
										
										//we pick the unique number for every facility
										CCC=ml.getCCC();
								
								
											//using CCC above we check for the last count
											CCCCount mc=service.getCCCCountByCCC(CCC);
											lastcount=mc.getLastCount();
                                            log.info("This the last count per the location and CCC "+lastcount);
											
											//we increament the count by one
											
								
											lastcount++;
											String pCCCIdentifier=""+lastcount;
												
											//check for the number of digits required to be concatnated to facility number
											if(pCCCIdentifier.length() < 5){
												int x=5-pCCCIdentifier.length();
													String y="";
												for(int k=0;k<x;k++)
													y +="0";
												pCCCIdentifier =y+pCCCIdentifier;
											}
											CCCIdentifier=CCC+"-"+pCCCIdentifier;


											glbCCC=adminservice.getGlobalProperty("cccgenerator.CCC");

											PatientIdentifierType patientIdentifierType=pService.getPatientIdentifierTypeByName(glbCCC);


                                            List<PatientIdentifier> listOfCCCIdentifier=pService.getPatientIdentifiers(null,Arrays.asList(patientIdentifierType),Arrays.asList(locservice.getLocation(i)),Arrays.asList(patients),false);

                                                  log.info("Already patients ids per CCC "+listOfCCCIdentifier.size());
                                        if(listOfCCCIdentifier.size()==0){

                                        //}

											PatientIdentifier patientIdentifier=new PatientIdentifier();

											
											patientIdentifier.setPatient(patients);
											patientIdentifier.setIdentifier(CCCIdentifier);
											patientIdentifier.setIdentifierType(patientIdentifierType);
											patientIdentifier.setLocation(locservice.getLocation(i));
											patientIdentifier.setPreferred(false);

											mc.setLastCount(lastcount); 
											//save the count thereby rewriting the previous one
											service.saveCCCCount(mc);
                                            Integer thecountLast=null;
                                            Integer CCCcount=null;
                                            CCCCount thecount=service.getCCCCountByCCC(CCC);
                                            thecountLast=thecount.getCCC();
                                            CCCcount=thecount.getLastCount();

                                              //log.info("This is the count  "+CCCcount+" and CCC is "+thecountLast);



											//add and save patient identserifier
											pService.savePatientIdentifier(patientIdentifier);


                                            number_of_hiv_patients_affected +=1;


                                            number_of_hiv_patients_affected_generated += number_of_hiv_patients_affected;
                                            //add the patients to the list so that we can use in the jsp
                                            listOfHIVPatientsPerSite.add(patients);
                                            ////////////////////////////////////////////////////////////////////////

                                            //CCCCount allCCC=new CCCCount();
                                            //update all other related sites
                                            List<CCCCount> allCCC= service.getAllRelatedSites(thecountLast);
                                                //log.info("uuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuu "+allCCC.size());
                                            //log.info("This is the CCC sizes "+allCCC.getLocation());
                                             for(CCCCount m:allCCC){
                                                 m.setLastCount(CCCcount);
                                                 service.saveCCCCount(m);
                                             }

                                           // log.info("After the count has been changed ");
                                            ////////////////////////////////////////////////////////////////////////

                                            cleanupaftersaving(number_of_hiv_patients_affected);
                                        }
                                        else {
                                            continue;
                                        }

                                    }

                        }


						   }
					
					break;
					
				
			}

			
		}
        List<Location> locations=locservice.getAllLocations(false);
        map.addAttribute("siteLocations",locations);
        map.addAttribute("totalcccnumbersgenerated", listOfHIVPatientsPerSite.size());
        httpSession.setMaxInactiveInterval(httpSessionvalue);
		
}
	
	

	private static class SortEncountersByDateComparator implements Comparator<Object> {

		//@Override
		public int compare(Object a, Object b) {
			Encounter ae = (Encounter) a;
			Encounter be = (Encounter) b;
			return ae.getEncounterDatetime().compareTo(be.getEncounterDatetime());
		}
	}

    private static class SortCCC implements Comparator<Object> {

        //@Override
        public int compare(Object a, Object b) {
            String glbccc=Context.getAdministrationService().getGlobalProperty("cccgenerator.CCC");
            PatientIdentifierType patientIdentifier=Context.getPatientService().getPatientIdentifierTypeByName(glbccc) ;
            Patient ae = (Patient) a;
            Patient be = (Patient) b;
            return ae.getPatientIdentifier(patientIdentifier.getName()).compareTo(be.getPatientIdentifier(patientIdentifier.getName()));
        }
    }

    private void cleanupaftersaving(int count){

        if(count % 10 == 0){
            Context.flushSession();
            Context.clearSession();
        }

    }

    private void cleanupafterCount(int counts){

        if(counts % 100 == 0){
            Context.flushSession();
            Context.clearSession();
        }

    }

    @RequestMapping(value="/module/cccgenerator/csvdownload")
    public void downloadCSV( HttpServletResponse response,HttpServletRequest request,
                             @RequestParam(required=true, value="sitelocationToDownload") String location,
                             @RequestParam(required=true, value="additionalNumbers") String additional
                                ) throws IOException {

        //get the location based on the location name
        Location cccLocation=Context.getLocationService().getLocation(location);
        /////////
        //lengthen the session to make sure generation is complete
        HttpSession httpSession=request.getSession();
        Integer httpSessionvalue=httpSession.getMaxInactiveInterval();
        httpSession.setMaxInactiveInterval(-1);


        //create a temporary file that will be destroyed after completion
        File file= File.createTempFile(cccLocation.getName(),".csv");
         ///////find the patients to be added on the file

        LocationService locationService=Context.getLocationService();
        PatientService patientService=Context.getPatientService();

       // List<Location> siteLocation=locationService.getAllLocations(false);

        String glbccc=Context.getAdministrationService().getGlobalProperty("cccgenerator.CCC");
        PatientIdentifierType patientIdentifier=patientService.getPatientIdentifierTypeByName(glbccc) ;
          //get all the location and related ones
         Set<Integer> setOfIdsLocsAndSubLocs=Context.getService(CCCGeneratorService.class).getIdsOfLocationsParentAndChildren(cccLocation.getLocationId());
            //log.info("This are all the locations and sub sites "+setOfIdsLocsAndSubLocs);
        //get patientIds based on patientIdentifiers and location
        //loop through all the locations as we pick all the patient ids
        Set<Integer> setOfPatientsWithIdentifierAndLocation=new HashSet<Integer>();

        for(Integer j:setOfIdsLocsAndSubLocs){
            Cohort listOfPatientsWithCCCandLocation=Context.getService(CCCGeneratorService.class).getPatientByIdentifierAndLocation(patientIdentifier.getPatientIdentifierTypeId(),j);
            setOfPatientsWithIdentifierAndLocation.addAll(listOfPatientsWithCCCandLocation.getMemberIds());
        }
        List<Patient> patientsFromSet=new ArrayList<Patient>();
        //find all the patients using patient ids found

        for(Integer i:setOfPatientsWithIdentifierAndLocation){

            patientsFromSet.add(Context.getPatientService().getPatient(i)) ;
        }

        Location l=Context.getLocationService().getLocation(location.trim());

        Collections.sort(patientsFromSet, new SortCCC());

         StringBuilder stringBuilderColumnsHeaders=new StringBuilder();
          StringBuilder stringBuilderContents;
          StringBuilder additionalCCC;
          StringBuilder stringBuilderContentsAdditionals=new StringBuilder();
          StringBuilder reportHeader=new StringBuilder();


          reportHeader.append("\""+l.getName()+"\""+"," +"\""+" and Other Related Sites"+ "\""+","+"\""+"CCC Number Registar"+"\""+",");

          stringBuilderColumnsHeaders.append("\"CCC Number\"").append(",").append("\"Names\"").append(",").append("\"AMRS ID\"").append(",").append("\"D.O.B\"").append(",");

            FileWriter fstream = new FileWriter(file,true);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(reportHeader.toString()+"\n");
            out.write(stringBuilderColumnsHeaders.toString()+"\n");

            for(Patient p:patientsFromSet){
                stringBuilderContents=new StringBuilder();
                stringBuilderContents.append("\""+p.getPatientIdentifier(patientIdentifier.getName())+"\"").append(",").append("\""+namesFormated(p.getNames().toString())+"\"").append(",").append("\""+p.getPatientIdentifier(getAMRSID(p))+"\"").append(",").append("\""+formatDate(p.getBirthdate())+"\"").append(",");
                out.write(stringBuilderContents.toString());
                out.write("\n");


            }
            //pick the last count of the ccc number location
            CCCGeneratorService service=Context.getService(CCCGeneratorService.class);
            CCCLocation ml=service.getCCCLocationByLocation(cccLocation);

            //we pick the unique number for every facility
           Integer  CCC=ml.getCCC();


            //using CCC above we check for the last count
            CCCCount mc=service.getCCCCountByCCC(CCC);
            Integer lastcount=mc.getLastCount();
            Integer counttogetto=Integer.parseInt(additional)+lastcount;
            for(int h=lastcount;h<counttogetto;h++){
                additionalCCC=new StringBuilder();
                lastcount++;
                String lcount=lastcount.toString();
                String cccNumber;
                if(lcount.length() < 5){
                    int x=5-lcount.length();
                    String y="";
                    for(int k=0;k<x;k++)
                        y +="0";
                    lcount =y+lcount;
                }
                cccNumber=CCC+"-"+lcount;

                additionalCCC.append("\""+cccNumber+"\"").append(",").append("\""+"\"").append(",").append("\""+"\"").append(",").append("\""+"\"").append(",");
                out.write(additionalCCC.toString());
                out.write("\n");

            }

            out.close();


        //////////////////////////////////////////////////////////////////////

        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=" + file);
        response.setContentLength((int) file.length());



        FileCopyUtils.copy(new FileInputStream(file), response.getOutputStream());
        file.delete();
        httpSession.setMaxInactiveInterval(httpSessionvalue);
    }

    private  String formatDate(Date d){

        String datetoformat=d.toString();
        String dateformated=datetoformat.substring(0,10);

        return dateformated;

    }
    private String namesFormated(String n){
        String nn=n.substring(1);
        String nnn=nn.substring(0,nn.length()-1);
            return nnn;
    }

    private String getAMRSID(Patient patient){

        AdministrationService ams=Context.getAdministrationService();
        String amrsnum= new String();

        PatientIdentifierType patientIdentifierType=Context.getPatientService().getPatientIdentifierTypeByName(ams.getGlobalProperty("cccgenerator.CCC"));

        List<PatientIdentifier>  listPi= patient.getActiveIdentifiers() ;

        for(PatientIdentifier pid:listPi) {

            if(!OpenmrsUtil.nullSafeEquals(pid.getIdentifierType(), patientIdentifierType))

                amrsnum=pid.getIdentifierType().getName();

         }
        return amrsnum;
    }


	
	
}

