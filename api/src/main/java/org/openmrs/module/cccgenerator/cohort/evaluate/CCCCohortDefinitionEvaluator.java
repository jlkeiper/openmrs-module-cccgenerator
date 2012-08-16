package org.openmrs.module.cccgenerator.cohort.evaluate;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.OpenmrsObject;
import org.openmrs.annotation.Handler;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PatientSetService;
import org.openmrs.api.context.Context;
import org.openmrs.module.cccgenerator.cohort.CCCCohortDefinition;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.*;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.DurationUnit;
import org.openmrs.module.reporting.common.SetComparator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Nicholas Ingosi Magaja
 * Date: 7/13/12
 * Time: 9:33 AM
 * To change this template use File | Settings | File Templates.
 */
@Handler(supports = {CCCCohortDefinition.class})
public class CCCCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

    private static final Log log = LogFactory.getLog(CCCCohortDefinitionEvaluator.class);

    public static final String ENCOUNTER_TYPE_ADULT_RETURN = "ADULTRETURN";

    public static final String ENCOUNTER_TYPE_ADULT_INITIAL = "ADULTINITIAL";
    public static final String ENCOUNTER_TYPE_PEDS_INITIAL = "PEDSINITIAL";

    public static final String FIRST_HIV_RAPID_TEST_QUALITATIVE_CONCEPT = "HIV RAPID TEST, QUALITATIVE";

    public static final String SECOND_HIV_RAPID_TEST_QUALITATIVE_CONCEPT = "HIV RAPID TEST 2, QUALITATIVE";

    public static final String POSITIVE_CONCEPT = "POSITIVE";

    public static final String HIV_ENZYME_IMMUNOASSAY_QUALITATIVE_CONCEPT = "HIV ENZYME IMMUNOASSAY, QUALITATIVE";

    public String OBS_CONCEPT = "concept";

    public String OBS_VALUE_CODED = "valueCoded";

    public static final String YES= "YES";
    public static final String REASON_EXITED_CARE= "REASON EXITED CARE";
    public static final String DISCONTINUE_FROM_CLINIC_HIV_NEGATIVE= "DISCONTINUE FROM CLINIC, HIV NEGATIVE";


    public EvaluatedCohort evaluate(final CohortDefinition cohortDefinition, final EvaluationContext evaluationContext) throws EvaluationException {

        CCCCohortDefinition cccCohortDefinition = (CCCCohortDefinition) cohortDefinition;

        EncounterService service = Context.getEncounterService();
        ConceptService conceptService = Context.getConceptService();
        CohortDefinitionService definitionService = Context.getService(CohortDefinitionService.class);


        EncounterCohortDefinition encounterCohortDefinition = new EncounterCohortDefinition();
        encounterCohortDefinition.addEncounterType(service.getEncounterType(ENCOUNTER_TYPE_ADULT_INITIAL));
        encounterCohortDefinition.addEncounterType(service.getEncounterType(ENCOUNTER_TYPE_ADULT_RETURN));
        encounterCohortDefinition.setLocationList(cccCohortDefinition.getLocationList());
        Cohort encounterCohort = definitionService.evaluate(encounterCohortDefinition, evaluationContext);
        log.info("Encounters per the encounters  "+encounterCohort.size());


        Concept firstRapidConcept = conceptService.getConcept(FIRST_HIV_RAPID_TEST_QUALITATIVE_CONCEPT);
        Concept secondRapidConcept = conceptService.getConcept(SECOND_HIV_RAPID_TEST_QUALITATIVE_CONCEPT);
        Concept positiveConcept = conceptService.getConcept(POSITIVE_CONCEPT);



        CodedObsCohortDefinition firstRapidCohortDefinition = new CodedObsCohortDefinition();
        firstRapidCohortDefinition.setTimeModifier(PatientSetService.TimeModifier.ANY);
        firstRapidCohortDefinition.setLocationList(cccCohortDefinition.getLocationList());
        firstRapidCohortDefinition.setQuestion(firstRapidConcept);
        firstRapidCohortDefinition.setOperator(SetComparator.IN);
        firstRapidCohortDefinition.setValueList(Arrays.asList(positiveConcept));

        CodedObsCohortDefinition secondRapidCohortDefinition = new CodedObsCohortDefinition();
        secondRapidCohortDefinition.setTimeModifier(PatientSetService.TimeModifier.ANY);
        secondRapidCohortDefinition.setLocationList(cccCohortDefinition.getLocationList());
        secondRapidCohortDefinition.setQuestion(secondRapidConcept);
        secondRapidCohortDefinition.setOperator(SetComparator.IN);
        secondRapidCohortDefinition.setValueList(Arrays.asList(positiveConcept));

        CompositionCohortDefinition rapidCompositionCohortDefinition = new CompositionCohortDefinition();
        rapidCompositionCohortDefinition.addSearch("PositiveFirstRapid", firstRapidCohortDefinition, null);
        rapidCompositionCohortDefinition.addSearch("PositiveSecondRapid", secondRapidCohortDefinition, null);
        rapidCompositionCohortDefinition.setCompositionString("PositiveFirstRapid OR PositiveSecondRapid");

        Cohort rapidCompositionCohort = definitionService.evaluate(rapidCompositionCohortDefinition, evaluationContext);

        log.info("Rapid composition pple "+rapidCompositionCohort.size());

        AgeCohortDefinition ageCohortDefinition = new AgeCohortDefinition();
        ageCohortDefinition.setMinAge(18);
        ageCohortDefinition.setMinAgeUnit(DurationUnit.MONTHS);
        ageCohortDefinition.setMaxAge(2);
        ageCohortDefinition.setMaxAgeUnit(DurationUnit.YEARS);

        Cohort ageCohort=definitionService.evaluate(ageCohortDefinition,evaluationContext);

        Concept elisaConcept = conceptService.getConcept(HIV_ENZYME_IMMUNOASSAY_QUALITATIVE_CONCEPT);

        CodedObsCohortDefinition elisaCohortDefinition = new CodedObsCohortDefinition();
        elisaCohortDefinition.setTimeModifier(PatientSetService.TimeModifier.ANY);
        elisaCohortDefinition.setLocationList(cccCohortDefinition.getLocationList());
        elisaCohortDefinition.setQuestion(elisaConcept);
        elisaCohortDefinition.setOperator(SetComparator.IN);
        elisaCohortDefinition.setValueList(Arrays.asList(positiveConcept));

        CompositionCohortDefinition elisaCompositionCohortDefinition = new CompositionCohortDefinition();
        elisaCompositionCohortDefinition.addSearch("PaediatricAge", ageCohortDefinition, null);
        elisaCompositionCohortDefinition.addSearch("PositiveElisa", elisaCohortDefinition, null);
        elisaCompositionCohortDefinition.setCompositionString("PaediatricAge AND PositiveElisa");

        Cohort elisaCompositionCohort = definitionService.evaluate(elisaCompositionCohortDefinition, evaluationContext);
           log.info("Elisa patients "+elisaCompositionCohort.size());

        ///get Hiv patients with all people having 2 and 15 years and paeds initial field
            EncounterCohortDefinition encounterCohortDefinition215 = new EncounterCohortDefinition();
            encounterCohortDefinition215.addEncounterType(service.getEncounterType(ENCOUNTER_TYPE_PEDS_INITIAL));
            encounterCohortDefinition215.setLocationList(cccCohortDefinition.getLocationList());



            AgeCohortDefinition ageCohortDefinition2and15 = new AgeCohortDefinition();
            ageCohortDefinition.setMinAge(2);
            ageCohortDefinition.setMinAgeUnit(DurationUnit.YEARS);
            ageCohortDefinition.setMaxAge(14);
            ageCohortDefinition.setMaxAgeUnit(DurationUnit.YEARS);

            CompositionCohortDefinition encounterCohortDefinitionAndAge = new CompositionCohortDefinition();
            encounterCohortDefinitionAndAge.addSearch("PaediatricEncounter",encounterCohortDefinition215,null);
            encounterCohortDefinitionAndAge.addSearch("Paediatric215",ageCohortDefinition2and15,null);
            encounterCohortDefinitionAndAge.setCompositionString("PaediatricEncounter AND Paediatric215");


             Cohort encounterCohortDefinitionAndAgeCohort = definitionService.evaluate(encounterCohortDefinitionAndAge, evaluationContext);

            log.info("2 and 15 years with paeds initial "+encounterCohortDefinitionAndAgeCohort.size());

        // ///////////////////////////////////////////////////////////////////////////////////
        ///exclude all the patient already dicontionued ///////////////

        Map<String, Collection<OpenmrsObject>> restrictions = new HashMap<String, Collection<OpenmrsObject>>();
        restrictions.put(OBS_CONCEPT, Arrays.<OpenmrsObject>asList(elisaConcept));
        restrictions.put(OBS_VALUE_CODED, Arrays.<OpenmrsObject>asList(positiveConcept));

        Set<Integer> uniqueSetids=new HashSet<Integer>(CollectionUtils.intersection(rapidCompositionCohort.getMemberIds(), ageCohort.getMemberIds()));

        Set<Integer> patientIds = new HashSet<Integer>();
        patientIds.addAll(encounterCohort.getMemberIds());
        patientIds.addAll(rapidCompositionCohort.getMemberIds());
        patientIds.addAll(elisaCompositionCohort.getMemberIds());
        //patientIds.addAll(encounterCohortDefinitionAndAgeCohort.getMemberIds());


        return new EvaluatedCohort(new Cohort(patientIds), cohortDefinition, evaluationContext);
    }

}
