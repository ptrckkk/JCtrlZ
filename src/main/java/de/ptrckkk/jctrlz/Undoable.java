package de.ptrckkk.jctrlz;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Classes that are annotated with this annotation are to be tracked for member variable changes that can be undone.
 *
 * @author ptrckkk
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Undoable {

    /**
     * The sole property of this annotation indicates whether assigning value, v, of a property of an annotated object
     * shall be added to the history list of the property of this object is already set to v. The default behavior is
     * to add re-setting the same property value to the history list.<br>
     * If you do not wish this behavior, set the value of the annotation to false.<br><br>
     * For example, consider the following piece of code:<br>
     * <code>
     * myObject.setValue(1);<br>
     * myObject.setValue(5);<br>
     * myObject.setValue(5);<br>
     * </code>
     * In case the value of this property is set to true, after undoing the history once myObject.value will have the
     * value 5. If the value of this annotataion is set to false, myObject.value equals 1.
     */
    boolean value() default true;

}
