package com.sap.sailing.cache.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Access (get/set) to fields decorated with this annotation will NOT be monitored even if happening within the control flow of a cached_method.<br>
 * As annotations on fields are not inherited, @IgnoreField must be applied to the parent class' field and to all subclasses which eventually hide the field.
 * 
 * @author Raul Bertone (D059912)
 */

@Target(ElementType.FIELD)
public @interface IgnoreField {

}
