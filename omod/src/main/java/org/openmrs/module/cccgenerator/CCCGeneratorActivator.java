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
package org.openmrs.module.cccgenerator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.ModuleActivator;

/**
 * This class contains the logic that is run every time this module is either started or shutdown
 */
public class CCCGeneratorActivator implements ModuleActivator {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * @see org.openmrs.module.Activator#startup()
	 */
	public void startup() {
		log.info("Starting CCC Generator Module");
	}
	
	/**
	 * @see org.openmrs.module.Activator#shutdown()
	 */
	public void shutdown() {
		log.info("Shutting down CCC Generator Module");
	}

	public void contextRefreshed() {
		log.info("CCC Generator Module context refreshed");
		
	}

	public void started() {
		log.info("CCC Generator Module started");
		
	}

	public void stopped() {
		log.info("CCC Generator Module Stopped");
		
	}

	public void willRefreshContext() {
		log.info("CCC Generator Module will be refreshed");
		
	}

	public void willStart() {
		log.info("CCC Generator Module will be started");
		
	}

	public void willStop() {
		log.info("CCC Generator Module will be stopped");
		
	}
	
}
