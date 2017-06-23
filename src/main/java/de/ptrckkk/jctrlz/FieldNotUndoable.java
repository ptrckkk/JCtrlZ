package de.ptrckkk.jctrlz;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation provides a refinement option that extends the {@link Undoable} annotation. By default, all fields of
 * a class, which are annotated with {@link Undoable}, are being considered setting properties. However, sometimes it
 * is desirable to neglect or omit particular fields. Annotate these fields with this annotation to do just that.<br>
 * A {@link FieldNotUndoable} annotation on fields whose classes are not annotated with {@link Undoable} has no effect
 * whatsoever.
 *
 * @author ptrckkk
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FieldNotUndoable {}
