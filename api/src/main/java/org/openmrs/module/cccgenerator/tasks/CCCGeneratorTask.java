package org.openmrs.module.cccgenerator.tasks;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.cccgenerator.CCCGeneratorProcessor;
import org.openmrs.scheduler.tasks.AbstractTask;

/**
 * Created with IntelliJ IDEA.
 * User: Nicholas Ingosi Magaja
 * Date: 5/25/12
 * Time: 12:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class CCCGeneratorTask extends AbstractTask {

    // Logger
    private static Log log = LogFactory.getLog(CCCGeneratorTask.class);



    private static CCCGeneratorProcessor processor = null;

    /**
     * Default Constructor (Uses SchedulerConstants.username and SchedulerConstants.password
     */
    public CCCGeneratorTask() {
        if (processor == null) {
            processor = new CCCGeneratorProcessor();
        }
    }
    /**
     * Process the next form entry in the database and then remove the form entry from the database.
     */
    @Override
    public void execute() {
        Context.openSession();
        try {
            log.debug("Processing CCC task ... ");
            if (!Context.isAuthenticated()) {
                authenticate();
            }
            processor.processCCCGenerator();
        }
        catch (Exception e) {
            log.error("Error running CCC task", e);
            throw new APIException("Error running CCC task", e);
        }
        finally {
            Context.closeSession();
        }
    }
    /*
          * Resources clean up
          */
    public void shutdown() {
        processor = null;
        super.shutdown();
        log.debug("Shutting down CCC task ...");
    }

}
