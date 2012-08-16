package org.openmrs.module.cccgenerator;

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

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Nicholas Ingosi Magaja
 * Date: 5/25/12
 * Time: 12:34 PM
 * To change this template use File | Settings | File Templates.
 *
 */
public class CCCGeneratorGenerate {

    private final Log log = LogFactory.getLog(getClass());

    public static final String ENCOUNTER_TYPE_ADULT_RETURN = "ADULTRETURN";
    public static final String ENCOUNTER_TYPE_ADULT_INITIAL = "ADULTINITIAL";
    public static final String ENCOUNTER_TYPE_PEDS_INITIAL = "PEDSINITIAL";
    public static final String ENCOUNTER_TYPE_PEDS_RETURN = "PEDSRETURN";


    public void processCCCS(){

        CCCGeneratorService service= Context.getService(CCCGeneratorService.class);

        EncounterService encounterservice=Context.getEncounterService();
        AdministrationService adminservice=Context.getAdministrationService();
        PatientService pService=Context.getPatientService();
        LocationService locservice=Context.getLocationService();


        Integer CCC=0;
        Integer lastcount=0;
        String CCCIdentifier="";
        String glbCCC="";
        int number_of_hiv_patients_affected = 0;
        int number_of_hiv_patients_affected_generated = 0;





        //cohort to return all patient ids

        log.info("Just entered in the whenpage loads");

        Cohort listOpPatientId=service.getAllHIVPatients();



        log.info(listOpPatientId.size()+"  HIV Patients found");

        //provide a set to hold all the ids from the cohort
        Set<Integer> patientsIds=new HashSet<Integer>(listOpPatientId.getMemberIds());


        //map.addAttribute("patientsIds", patientsIds.size());


        Patient patients;



        for(Integer patientId:patientsIds){

            patients=pService.getPatient(patientId);

            //log.info(patients.getNames());

            List<Encounter> listOfEncounters=encounterservice.getEncountersByPatientId(patientId);

            Collections.sort(listOfEncounters, new SortEncountersByDateComparator());



            for(Encounter encounters:listOfEncounters){

                if(encounters.getEncounterType().getName().equals(ENCOUNTER_TYPE_ADULT_INITIAL)
                        ||encounters.getEncounterType().getName().equals(ENCOUNTER_TYPE_ADULT_RETURN)
                        ||encounters.getEncounterType().getName().equals(ENCOUNTER_TYPE_PEDS_INITIAL)
                        ||encounters.getEncounterType().getName().equals(ENCOUNTER_TYPE_PEDS_RETURN)){
                    //
                    //get a list of all the CCCloation table ie the codes entered for every facility
                    List<CCCLocation> mllist=service.getAllCCCLocations();

                    for(CCCLocation CCClocation:mllist){

                        //check if our location/site tallies with ones from the encounters given above
                        if(CCClocation.getLocation()==encounters.getLocation()){

                            CCCLocation ml=service.getCCCLocationByLocation(encounters.getLocation());

                            //we pick the unique number for every facility
                            CCC=ml.getCCC();


                            //using CCC above we check for the last count
                            CCCCount mc=service.getCCCCountByCCC(CCC);
                            lastcount=mc.getLastCount();

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

                            glbCCC=adminservice.getGlobalProperty("cccgenerator.CCC");//getGlobalProperty("cccgenerator.CCC");

                            PatientIdentifierType patientIdentifierType=pService.getPatientIdentifierTypeByName(glbCCC);

                            PatientIdentifier patientIdentifier=new PatientIdentifier();


                            patientIdentifier.setPatient(patients);
                            patientIdentifier.setIdentifier(CCCIdentifier);
                            patientIdentifier.setIdentifierType(patientIdentifierType);
                            patientIdentifier.setLocation(locservice.getLocation(encounters.getLocation().getLocationId()));
                            patientIdentifier.setPreferred(false);

                            mc.setLastCount(lastcount);
                            //save the count thereby rewriting the previous one
                            service.saveCCCCount(mc);
                            //add and save patient identifier
                            pService.savePatientIdentifier(patientIdentifier);

                            number_of_hiv_patients_affected +=1;

                            number_of_hiv_patients_affected_generated += number_of_hiv_patients_affected;

                            cleanupaftersaving(number_of_hiv_patients_affected);


                        }



                    }


                }

                break;


            }


        }


    }



    private static class SortEncountersByDateComparator implements Comparator<Object> {

        //@Override
        public int compare(Object a, Object b) {
            Encounter ae = (Encounter) a;
            Encounter be = (Encounter) b;
            return ae.getEncounterDatetime().compareTo(be.getEncounterDatetime());
        }
    }

    private void cleanupaftersaving(int count){

        if(count % 10 == 0){
            Context.flushSession();
            Context.clearSession();
        }

    }


}

