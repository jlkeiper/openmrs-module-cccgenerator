package org.openmrs.module.cccgenerator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.cccgenerator.model.CCCCount;
import org.openmrs.module.cccgenerator.model.CCCLocation;
import org.openmrs.module.cccgenerator.service.CCCGeneratorService;

import java.util.Map;
import java.util.Set;

/**
 * Generator for CCC numbers
 */
public class CCCGeneratorGenerate {

	private final Log log = LogFactory.getLog(getClass());

	public void generateCCCNumbers() {

		CCCGeneratorService service = Context.getService(CCCGeneratorService.class);

		// get the patient identifier type for use in our generating
		String pitName = Context.getAdministrationService().getGlobalProperty("cccgenerator.CCC");
		PatientIdentifierType pit = Context.getPatientService().getPatientIdentifierTypeByName(pitName);

		// get enrolled HIV patients, grouped by location
		Map<Location, Set<Integer>> locationPatientMap = service.getLocationPatientMap();

		long count = 0;

		// iterate through locations
		for (Location location: locationPatientMap.keySet()) {

			// if a facility code exists for that location ...
			CCCLocation facility = service.getCCCLocationByLocation(location);
			if (facility != null) {

				Integer prefix = facility.getCCC();

				// get the next serial for this facility
				CCCCount counter = service.getCCCCountByCCC(prefix);
				Integer serial = counter.getLastCount() + 1;

				// cycle through people to set identifiers
				for (Integer patientId : locationPatientMap.get(location)) {

					// get the patient
					Patient patient = Context.getPatientService().getPatient(patientId);

					// only do something if the patient is valid
					if (patient != null && !patient.isVoided()) {

						// create a new identifier
						PatientIdentifier pi = new PatientIdentifier();
						pi.setIdentifierType(pit);
						pi.setIdentifier(String.format("%05d-%05d", prefix, serial++));
						pi.setLocation(location);
						pi.setPatient(patient);

						// add it to the patient
						patient.addIdentifier(pi);

						// save and move on
						Context.getPatientService().savePatient(patient);

						cleanup(count++);
					}
				}

				// update the serial for this facility
				counter = service.getCCCCountByCCC(prefix);
				counter.setLastCount(serial);

				// save and move on
				service.saveCCCCount(counter);
			}
		}
	}

	private void cleanup(long count) {
		if (count % 10 == 0) {
			Context.flushSession();
			Context.clearSession();
		}
	}
}

