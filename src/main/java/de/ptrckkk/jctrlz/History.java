package de.ptrckkk.jctrlz;

import java.lang.reflect.Field;
import java.util.Stack;

/**
 * This static class provides the functionality to undo the captured history.
 *
 * @author ptrckkk
 */
public class History {

    /**
     * Do not allow to create instances of this class.
     */
    private History() {}

    /**
     * This stack of {@link HistoryEntry} elements the history that we use to execute the undo commands.
     */
    private static Stack<HistoryEntry> historyStack = new Stack<>();

    /**
     * @return Returns the current size of the history, i. e., the number of steps that can be undone.
     */
    public static int getHistorySize() {
        return historyStack.size();
    }

    /**
     * Adds an element to the history stack.
     *
     * @param object     The object whom's field was changed.
     * @param fieldName  The name of the field that was changed.
     * @param fieldValue The old value before setting the new value.
     */
    static void addHistoryElement(Object object, String fieldName, Object fieldValue) {
        historyStack.push(new HistoryEntry(object, fieldName, fieldValue));
    }

    /**
     * This method executes an undo operation.
     *
     * @return Returns true if the undo operation was successful. This is the case if the history is not empty and no
     * error occurred while setting the previous object value of the last history event.
     */
    public static boolean undo() {
        if (historyStack.empty()) {
            return false;
        }

        HistoryEntry poppedElement = historyStack.pop();
        try {
            Field field = poppedElement.getObject().getClass().getDeclaredField(poppedElement.getFieldName());
            field.setAccessible(true);
            field.set(poppedElement.getObject(), poppedElement.getFieldValue());
        } catch (Exception ignored) {
            return false;
        }

        return true;
    }

}

/**
 * This is a simple POJO that represents an entry in the history stack. The property names should be self-explanatory.
 */
class HistoryEntry {

    private Object object;
    private String fieldName;
    private Object fieldValue;

    HistoryEntry(Object object, String fieldName, Object fieldValue) {
        this.object = object;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    Object getObject() {
        return this.object;
    }

    String getFieldName() {
        return fieldName;
    }

    Object getFieldValue() {
        return fieldValue;
    }

}
