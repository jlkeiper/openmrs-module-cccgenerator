package org.openmrs.module.cccgenerator.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.*;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.cccgenerator.model.CCCLocation;
import org.openmrs.module.cccgenerator.service.CCCGeneratorService;
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

/**
 * Created with IntelliJ IDEA.
 * User: Nicholas Ingosi Magaja
 * Date: 7/4/12
 * Time: 8:36 AM
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class CSVDownloadController {
    private final Log log = LogFactory.getLog(getClass());

    @RequestMapping(method= RequestMethod.GET, value = "module/cccgenerator/csvdownload.form")
    public void whenPageLoads(ModelMap map){
        LocationService locationService= Context.getLocationService();
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
        map.addAttribute("siteLocation",locationsToIncludeOnlyParents);
    }
     //////////////////////////////////////////////////////////////////////////////////////////////////
     @RequestMapping(method=RequestMethod.POST, value = "module/cccgenerator/csvdownload.form")
     public void whenPageIsPosted(ModelMap map, HttpServletResponse response,
                                  @RequestParam(required=true, value="site") String siteId
     ) throws IOException {

              map.addAttribute("site",Context.getLocationService().getLocation(Integer.parseInt(siteId)).getName());

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
    @RequestMapping(value="/module/cccgenerator/csvdownloadafter")
    public void downloadCSV( HttpServletResponse response,HttpServletRequest request,
                             @RequestParam(required=true, value="site") String location,
                             @RequestParam(required=true, value="additionalNumbers") String additions
                                ) throws IOException {
        //get the location based on the location string
        Location cccLocation=Context.getLocationService().getLocation(location);
        //lengthen the session to make sure generation is complete
        HttpSession httpSession=request.getSession();
        Integer httpSessionvalue=httpSession.getMaxInactiveInterval();
        httpSession.setMaxInactiveInterval(-1);

        //create a temporary file that will be reclaimed after using
        File file= File.createTempFile(cccLocation.getName(),".csv");

        LocationService locationService=Context.getLocationService();
        PatientService patientService=Context.getPatientService();

        //get list of all the locations
        List<Location> siteLocation=locationService.getAllLocations(false);

        //get identifiertype based on the identifier name based on the global variable
        String glbccc=Context.getAdministrationService().getGlobalProperty("cccgenerator.CCC");
        PatientIdentifierType patientIdentifier=patientService.getPatientIdentifierTypeByName(glbccc) ;

        //get patientIds based on patientIdentifiers and location
        Cohort listOfPatientsWithCCCandLocation=Context.getService(CCCGeneratorService.class).getPatientByIdentifierAndLocation(patientIdentifier.getPatientIdentifierTypeId(),cccLocation.getLocationId());

        //put the results in the set to avoid duplicates of patient ids
        Set<Integer> setOfPatientsWithIdentifierAndLocation=new HashSet<Integer>(listOfPatientsWithCCCandLocation.getMemberIds());

          log.info("Number of patients with CCCs from the location given "+setOfPatientsWithIdentifierAndLocation.size());

        List<Patient> patientsFromSet=new ArrayList<Patient>();
        //find all the patients using patient ids found

        for(Integer i:setOfPatientsWithIdentifierAndLocation){

            patientsFromSet.add(Context.getPatientService().getPatient(i)) ;
        }

        ////////////////////////////////////////////////////////////////////////////////////////////////////
         Location l=Context.getLocationService().getLocation(location.trim());


        ////////////////////////////////////////////////////////////////////////////////////////////////////
        log.info("The size of the really sites are "+patientsFromSet.size());

        Collections.sort(patientsFromSet, new SortCCC());

        StringBuilder stringBuilderColumnsHeaders=new StringBuilder();
        StringBuilder stringBuilderAdditionalCCCNumbers;
        StringBuilder additionalCCC;
        StringBuilder stringBuilderContentsAdditionals=new StringBuilder();

        stringBuilderColumnsHeaders.append("\"CCC Number\"").append(",").append("\"Names\"").append(",").append("\"AMRS ID\"").append(",").append("\"D.O.B\"").append(",");

        StringBuilder reportHeader=new StringBuilder();

        reportHeader.append("\""+cccLocation.getName()+"\""+"," +"\""+"and Other Related Sites"+ "\""+"," +"\""+"CCC Number Registar"+"\""+",");

        FileWriter fstream = new FileWriter(file,true);
        BufferedWriter out = new BufferedWriter(fstream);
        out.write(reportHeader.toString()+"\n");
        out.write(stringBuilderColumnsHeaders.toString()+"\n");


        StringBuilder stringBuilderContents=null;
        //to find all the patients in the database having the CCC and the location provided
        for(Patient p:patientsFromSet){
            stringBuilderContents=new StringBuilder();
            stringBuilderContents.append("\""+p.getPatientIdentifier(patientIdentifier.getName())+"\"").append(",").append("\""+namesFormated(p.getNames().toString())+"\"").append(",").append("\""+p.getPatientIdentifier(getAMRSID(p))+"\"").append(",").append("\""+formatDate(p.getBirthdate())+"\"").append(",");
            out.write(stringBuilderContents.toString());
            out.write("\n");


        }

        //pick the last record as they are sorted per the CCC/ccc number in order to generate additionals

        //put the last record in the stringbuider
        String lastcccNuber=null;

        Integer lastpart=null;
        int desiredfigure=0;
        String firstpart=null;

        // try{
        int len=patientsFromSet.size()-1;
        lastcccNuber=(patientsFromSet.get(len).getPatientIdentifier(patientIdentifier.getName())).toString();
        String []lastcccNumbersplit=lastcccNuber.split("-");

          firstpart=lastcccNumbersplit[0];
          lastpart=Integer.parseInt(lastcccNumbersplit[1]);
          desiredfigure=Integer.parseInt(additions)+lastpart;
         //}


        for(int h=lastpart;h<desiredfigure;h++){
            lastpart++;
            stringBuilderAdditionalCCCNumbers=new StringBuilder();
            String lastcount=lastpart.toString();
            if(lastcount.length() < 5){
                int x=5-lastcount.length();
                String y="";
                for(int k=0;k<x;k++)
                    y +="0";
                lastcount =y+lastcount;
            }
            stringBuilderAdditionalCCCNumbers.append("\""+firstpart+"-"+lastcount+"\"").append(",").append("\""+"\"").append(",").append("\""+"\"").append(",").append("\""+"\"").append(",");
            out.write( stringBuilderAdditionalCCCNumbers.toString());
            out.write("\n");

        }
        out.close();
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=" + file);
        response.setContentLength((int) file.length());



        FileCopyUtils.copy(new FileInputStream(file), response.getOutputStream());
        file.delete();
        httpSession.setMaxInactiveInterval(httpSessionvalue);

         /*}
         catch(Exception e){
             e.printStackTrace();
         }
*/








    }
    private String getAMRSID(Patient patient){

        AdministrationService ams=Context.getAdministrationService();
        String cccnum= new String();

        PatientIdentifierType patientIdentifierType=Context.getPatientService().getPatientIdentifierTypeByName(ams.getGlobalProperty("cccgenerator.CCC"));

        List<PatientIdentifier>  listPi= patient.getActiveIdentifiers() ;

        for(PatientIdentifier pid:listPi) {

            if(!OpenmrsUtil.nullSafeEquals(pid.getIdentifierType(), patientIdentifierType))

                cccnum=pid.getIdentifierType().getName();

        }
        return cccnum;
    }

}
