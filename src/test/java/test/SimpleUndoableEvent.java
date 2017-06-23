package test;

import de.ptrckkk.jctrlz.Undoable;

import java.util.Calendar;

/**
 * This class models a simple event which has a name, start time and duration which are reflected by the classes member
 * variables. Duplicate entries in the history stack are permitted.
 *
 * @author ptrckkk
 */
@Undoable
public class SimpleUndoableEvent {

    private String name;
    private Calendar startTime;
    private float duration;

    public SimpleUndoableEvent(String name, Calendar startTime, float duration) {
        this.name = name;
        this.startTime = startTime;
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Calendar getStartTime() {
        return startTime;
    }

    public void setStartTime(Calendar startTime) {
        this.startTime = startTime;
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

}
