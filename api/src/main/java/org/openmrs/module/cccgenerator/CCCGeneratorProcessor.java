package org.openmrs.module.cccgenerator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Processor for CCC number generation
 */
public class CCCGeneratorProcessor {

	private static final Log log = LogFactory.getLog(CCCGeneratorProcessor.class);

	private CCCGeneratorGenerate CCCGeneratorProcessor = null;

	public void processCCCGenerator() {

		log.debug("Processing and creating CCC numbers");

		if (CCCGeneratorProcessor == null)
			CCCGeneratorProcessor = new CCCGeneratorGenerate();
		CCCGeneratorProcessor.generateCCCNumbers();

	}
}
