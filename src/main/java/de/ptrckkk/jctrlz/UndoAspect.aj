package de.ptrckkk.jctrlz;

import org.aspectj.lang.JoinPoint;

import java.lang.reflect.Field;

/**
 * @author ptrckkk
 */
public aspect UndoAspect {

    /**
     * This function checks if the given the class of a given join point is annotated with the {@link Undoable}
     * annotation.
     *
     * @param joinPoint The joint point that is related to the class to check.
     * @return Returns true if the class is annotated, false otherwise.
     */
    private boolean isClassAnnotated(JoinPoint joinPoint) {
        return joinPoint.getTarget() != null && joinPoint.getTarget().getClass().isAnnotationPresent(Undoable.class);
    }

    /**
     * Checks if a given field is annotated with {@link FieldNotUndoable}.
     *
     * @param field The field to check.
     * @return Returns true, if the annotation is present, false otherwise.
     */
    private boolean isFieldAnnotated(Field field) {
        return field.isAnnotationPresent(FieldNotUndoable.class);
    }

    /**
     * This method checks if a local variable is annotated with the {@link ObjectNotUndoable} annotation.
     * The processing of annotations of type "ElementType.LOCAL_VARIABLE" seems to be not fully supported (yet, as of
     * June 2017), see [1] and [2].
     * [1] https://stackoverflow.com/a/17365354
     * [2] https://stackoverflow.com/a/13878403
     *
     * @param localVar The local variable to check.
     * @return Returns true if the annotation is present, false otherwise.
     */
    private boolean isLocalVariableAnnotated(Object localVar) {
        // TODO: Fill with logic
        return true;
    }
    
    /**
     * This function checks if for a given class duplicate setters shall be ignored.
     *
     * @param joinPoint The joint point that is related to the class to check. It is assumed, that the class really has
     *                  an {@link Undoable} annotation (otherwise, a {@link NullPointerException} might be thrown)!
     * @return Returns true if resetting the same value of a property shall be ignored, false otherwise.
     */
    private boolean ignoreDuplicates(JoinPoint joinPoint) {
        return joinPoint.getTarget().getClass().getAnnotation(Undoable.class).value();
    }

    /**
     * This method determines whether a class field stores a value of a primitive type.
     *
     * @param field The field to check.
     * @return Returns true if the field stores a primitive type, false otherwise.
     */
    private boolean isFieldOfPrimitiveType(Field field) {
        String type = field.getType().getName();
        if (type.contains(".")) {
            // Primitive types do not contain a dot
            return false;
        } else {
            return type.equals("int") || type.equals("long") || type.equals("float") || type.equals("double") ||
                    type.equals("boolean") || type.equals("char") || type.equals("short") || type.equals("byte");
        }
    }

    /**
     * This function contains the logic to determine whether a new history entry is to be made or not, taking the
     * current configuration into consideration.
     *
     * @param ignoreDupl Indicates whether to ignore duplicates.
     * @param isPrimType Indicates whether the variable is of a primitive type.
     * @param valBefore The old value (before the setter).
     * @param valAfter The new value (after the setter).
     * @param hashBefore The old hash (before the setter).
     * @param hashAfter The new hash (after the setter).
     * @return Returns true if the criteria are satisfied to make a new history entry.
     */
    private boolean addToHistory(boolean ignoreDupl, boolean isPrimType, Object valBefore, Object valAfter, int hashBefore, int hashAfter) {
        // I know, all this could be converted into one boolean expression; I refrained from doing so in order to make
        // the code better readable and thus maintainable

        if (ignoreDupl) {
            return true;
        }

        if (isPrimType) {
            return !valBefore.equals(valAfter);
        }

        if (hashBefore != hashAfter) {
            return true;
        }
        // Null values could be involved, we have to be careful here
        return (valBefore == null && valAfter != null) ||
                (valBefore != null && valAfter == null);
    }

    public pointcut setProperty(Object o):
            // We want to take setters into consideration that are NOT done from within a constructor
            set(* *.*) && !cflow(call(*.new(..))) && target(o);

    Object around(Object o): setProperty(o) {
        Object returnValue;

        if (isClassAnnotated(thisJoinPoint)) {
            boolean didExceptionOccur = false;
            int beforeProceedHash = 0, afterProceedHash = 0;
            Object beforeProceedValue = null, afterProceedValue = null;

            String fieldName = thisJoinPoint.getSignature().getName();
            Field field = null;
            boolean isPrimitiveType = false;
            boolean ignoreDuplicates = ignoreDuplicates(thisJoinPoint);
            boolean skipField = false;

            // Before we execute the set operation, store the necessary information which makes it possible to decide
            // whether to add a history entry and which value
            try {
                field = thisJoinPoint.getTarget().getClass().getDeclaredField(fieldName);
                isPrimitiveType = isFieldOfPrimitiveType(field);
                skipField = isFieldAnnotated(field);
                if (!skipField) {
                    if (isPrimitiveType) {
                        beforeProceedValue = field.get(o);
                    } else {
                        beforeProceedValue = field.get(o);
                        // This check avoids NullPointerExceptions
                        beforeProceedHash = (beforeProceedValue != null) ? field.get(o).hashCode() : 0;
                    }
                }
            } catch (Exception ignored) {
                didExceptionOccur = true;
            }

            // Execute setter operation
            returnValue = proceed(o);

            // After setting: If there was no error, add a history entry if necessary
            if (!didExceptionOccur && !skipField) {
                try {
                    if (isPrimitiveType) {
                        afterProceedValue = field.get(o);
                    } else {
                        afterProceedValue = field.get(o);
                        afterProceedHash = (afterProceedValue != null) ? afterProceedValue.hashCode() : 0;
                    }
                    if (addToHistory(ignoreDuplicates, isPrimitiveType, beforeProceedValue, afterProceedValue, beforeProceedHash, afterProceedHash)) {
                        History.addHistoryElement(o, fieldName, beforeProceedValue);
                    }
                } catch (Exception ignored) {}
            }
        } else {
            returnValue = proceed(o);
        }

        return returnValue;
    }

}
