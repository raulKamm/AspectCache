package com.sap.sailing.cache.common;

import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * Access (get/set) to fields decorated with this annotation will be monitored and eventual dependencies registered and invalidations fired.<br>
 * As annotations on fields are not inherited, @DataField must be applied to the parent class' field and to all eventual subclasses which hide the field.
 * 
 * @author Raul Bertone (D059912)
 */

@Target(ElementType.FIELD)
public @interface DataField {

}
