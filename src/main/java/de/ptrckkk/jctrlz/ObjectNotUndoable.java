package de.ptrckkk.jctrlz;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation provides a refinement option that extends the {@link Undoable} annotation. By default, all objects,
 * which belong to a class that is annotated with {@link Undoable} are considered for building the history. However,
 * sometimes it is not desirable to track all instances of a class. This annotation overcomes this restriction.<br>
 * If an instance shall not be tracked, simply annotate it with this annotation.
 *
 * @author ptrckkk
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.LOCAL_VARIABLE)
public @interface ObjectNotUndoable {}
