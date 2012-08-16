package org.openmrs.module.cccgenerator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created with IntelliJ IDEA.
 * User: Nicholas Ingosi Magaja
 * Date: 5/25/12
 * Time: 12:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class CCCGeneratorProcessor {

    private static final Log log = LogFactory.getLog(CCCGeneratorProcessor.class);

    private CCCGeneratorGenerate CCCGeneratorProcessor  = null;

    public void processCCCGenerator() {

        log.debug("Processing and creating CCCS");

        if (CCCGeneratorProcessor == null)
            CCCGeneratorProcessor = new CCCGeneratorGenerate();
            CCCGeneratorProcessor.processCCCS();

    }
}
