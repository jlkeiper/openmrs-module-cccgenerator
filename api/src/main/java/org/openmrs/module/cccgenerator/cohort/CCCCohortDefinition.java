package org.openmrs.module.cccgenerator.cohort;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.module.reporting.cohort.definition.BaseCohortDefinition;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

@Localized("reporting.CCCCohortDefinition")
public class CCCCohortDefinition extends BaseCohortDefinition{

    private static final Log log = LogFactory.getLog(CCCCohortDefinition.class);



    @ConfigurationProperty(group = "otherGroup")
    private List<Location> locationList;

    public List<Location> getLocationList() {
        return locationList;
    }

    public void setLocationList(final List<Location> locationList) {
        this.locationList = locationList;
    }

    public void addLocation(Location location) {
        if (locationList == null) {
            locationList = new ArrayList<Location>();
        }

              locationList.add(location);

            }


 }

