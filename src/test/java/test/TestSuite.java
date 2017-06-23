package test;

import de.ptrckkk.jctrlz.History;
import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.*;

/**
 * Test-suite for the "add to history" and "undo history" mechanisms.
 *
 * @author ptrckkk
 */
public class TestSuite {

    private static final String EVENT1_NAME = "event 1";
    private static final int EVENT1_START_TIME_HOUR = 10;
    private static final int EVENT1_START_TIME_MINUTE = 30;
    private static final float EVENT1_DURATION = 1;

    /**
     * Completely clears the history. Should be called at the end or beginning of each test (but consistent ;)
     */
    private void clearHistory() {
        while (History.getHistorySize() > 0) {
            History.undo();
        }
    }

    @Before
    public void beforeTest() {
        clearHistory();
    }

    /**
     * This method contains tests on a class that is annotated with @{@link de.ptrckkk.jctrlz.Undoable}
     * (the annotation's value is the default value).
     */
    @Test
    public void testSimpleUndoableEvent() {
        Calendar startTime1 = Calendar.getInstance();
        startTime1.set(Calendar.HOUR, EVENT1_START_TIME_HOUR);
        startTime1.set(Calendar.MINUTE, EVENT1_START_TIME_MINUTE);

        Calendar startTime2 = Calendar.getInstance();
        startTime2.set(Calendar.HOUR, EVENT1_START_TIME_HOUR + 1);
        startTime2.set(Calendar.MINUTE, EVENT1_START_TIME_MINUTE + 15);

        // The object under test
        SimpleUndoableEvent event = new SimpleUndoableEvent(EVENT1_NAME, startTime1, EVENT1_DURATION);

        // Undo, nothing should have changed
        assertFalse(History.undo());
        assertEquals(EVENT1_NAME, event.getName());
        assertTrue(event.getStartTime().equals(startTime1));
        assertEquals(EVENT1_DURATION, event.getDuration(), 0.01);

        // Change duration, undo and check again
        event.setDuration(2);
        assertTrue(History.undo());
        assertEquals(EVENT1_DURATION, event.getDuration(), 0.01);

        // Change name and start time, then undo and check step by step
        String eventNameNew = EVENT1_NAME + " - modified";
        event.setName(eventNameNew);
        event.setStartTime(startTime2);
        assertTrue(event.getStartTime().get(Calendar.HOUR) == startTime2.get(Calendar.HOUR));
        assertTrue(History.undo());
        assertTrue(event.getStartTime().get(Calendar.HOUR) == EVENT1_START_TIME_HOUR);
        assertTrue(History.undo());
        assertEquals(EVENT1_NAME, event.getName());
        assertFalse(History.undo());

        // Change the start time by receiving the object; this should not create a history entry
        event.getStartTime().set(Calendar.HOUR, EVENT1_START_TIME_HOUR + 1);
        assertFalse(History.undo());

        // Setting a property several times should create n new history entries even when it's the same value
        event.setDuration((float) (EVENT1_DURATION + 1.5));
        event.setDuration(event.getDuration());
        event.setDuration(event.getDuration());
        assertEquals(3, History.getHistorySize());
        event.setName(event.getName());
        assertEquals(4, History.getHistorySize());
        event.setStartTime(event.getStartTime());
        assertEquals(5, History.getHistorySize());

        // See if behavior with "null" is also correct
        event.setStartTime(null);
        assertEquals(6, History.getHistorySize());
        event.setStartTime(null);
        assertEquals(7, History.getHistorySize());
        assertTrue(History.undo());
        assertTrue(History.undo());
        assertTrue(History.undo());
    }

    /**
     * This method contains tests on a class that is annotated with @{@link de.ptrckkk.jctrlz.Undoable}. Important
     * change in comparison with testSimpleUndoableEvent: The annotation's value is set to false, i. e., do not track
     * duplicate setters.
     */
    @Test
    public void testIgnoreDuplicatesUndoableEventUndoableEvent() {
        Calendar startTime1 = Calendar.getInstance();
        startTime1.set(Calendar.HOUR, EVENT1_START_TIME_HOUR);
        startTime1.set(Calendar.MINUTE, EVENT1_START_TIME_MINUTE);

        Calendar startTime2 = Calendar.getInstance();
        startTime2.set(Calendar.HOUR, EVENT1_START_TIME_HOUR + 1);
        startTime2.set(Calendar.MINUTE, EVENT1_START_TIME_MINUTE + 15);

        // The object under test
        IgnoreDuplicatesUndoableEvent event = new IgnoreDuplicatesUndoableEvent(EVENT1_NAME, startTime1, EVENT1_DURATION);

        // Undo, nothing should have changed
        assertFalse(History.undo());
        assertEquals(EVENT1_NAME, event.getName());
        assertTrue(event.getStartTime().equals(startTime1));
        assertEquals(EVENT1_DURATION, event.getDuration(), 0.01);

        // Change duration, undo and check again
        float newDuration = EVENT1_DURATION + 1;
        event.setDuration(newDuration);
        assertTrue(History.undo());
        assertEquals(EVENT1_DURATION, event.getDuration(), 0.01);

        // Change name and start time, then undo and check step by step
        String eventNameNew = EVENT1_NAME + " - modified";
        event.setName(eventNameNew);
        event.setStartTime(startTime2);
        assertTrue(event.getStartTime().get(Calendar.HOUR) == startTime2.get(Calendar.HOUR));
        assertTrue(History.undo());
        assertTrue(event.getStartTime().get(Calendar.HOUR) == EVENT1_START_TIME_HOUR);
        assertTrue(History.undo());
        assertEquals(EVENT1_NAME, event.getName());
        assertFalse(History.undo());

        // Change the start time by receiving the object; this should not create a history entry
        event.getStartTime().set(Calendar.HOUR, EVENT1_START_TIME_HOUR + 1);
        assertFalse(History.undo());

        // Setting a property n times should create n new history entries even when it's the same value
        event.setDuration((float) (EVENT1_DURATION + 1.5));
        event.setDuration(event.getDuration());
        event.setDuration(event.getDuration());
        assertEquals(1, History.getHistorySize());
        event.setName(event.getName());
        assertEquals(1, History.getHistorySize());
        event.setStartTime(event.getStartTime());
        assertEquals(1, History.getHistorySize());
        assertTrue(History.undo());

        // See if behavior with "null" is also correct
        event.setStartTime(null);
        assertEquals(1, History.getHistorySize());
        event.setStartTime(null);
        assertEquals(1, History.getHistorySize());
        assertTrue(History.undo());
        assertFalse(History.undo());
    }

    /**
     * This method contains tests on a class that is annotated with a default @{@link de.ptrckkk.jctrlz.Undoable}.
     * Some fields are annotated with {@link de.ptrckkk.jctrlz.FieldNotUndoable} but not all.
     */
    @Test
    public void testDoNotUndoParticularFieldsEvent() {
        Calendar startTime1 = Calendar.getInstance();
        startTime1.set(Calendar.HOUR, EVENT1_START_TIME_HOUR);
        startTime1.set(Calendar.MINUTE, EVENT1_START_TIME_MINUTE);

        Calendar startTime2 = Calendar.getInstance();
        startTime2.set(Calendar.HOUR, EVENT1_START_TIME_HOUR + 1);
        startTime2.set(Calendar.MINUTE, EVENT1_START_TIME_MINUTE + 15);

        // The object under test
        NotUndoableFieldEvent event = new NotUndoableFieldEvent(EVENT1_NAME, startTime1, EVENT1_DURATION);
        // Undo, nothing should have changed
        assertFalse(History.undo());

        // Change the duration; no history entry should be created
        event.setDuration(event.getDuration() + 1);
        assertFalse(History.undo());
        // Change the start time; no history entry should be created
        event.setStartTime(startTime2);
        assertFalse(History.undo());
        // Change the event name; a history entry should be created
        event.setName(EVENT1_NAME + " - modified");
        assertTrue(History.undo());
        assertEquals(EVENT1_NAME, event.getName());
    }

    /**
     * The reason for this test is the following: A null value of a field might have the same value as a value which is
     * assigned to that field afterwards. In the case that duplicates are not to be added to the history, new history
     * entries are to be added nonetheless since the two values (null and something not null) are different. This test
     * case ensures that this behavior is implemented correctly.
     */
    @Test
    public void testMockHashCodeObject() {
        MockHashCodeClass out = new MockHashCodeClass(10);
        out.setValue(0);
        assertEquals(1, History.getHistorySize());
        out.setValue(null);
        assertEquals(2, History.getHistorySize());
        out.setValue(0);
        assertEquals(3, History.getHistorySize());
    }

    /**
     * This test checks if the undo operation can be applied even though the object of the last access was in a
     * different scope.
     */
    @Test
    public void testScoping() {
        // event is only accessible from within the if block...
        if (true) {
            SimpleUndoableEvent event = new SimpleUndoableEvent(EVENT1_NAME, null, 1);
            event.setName(EVENT1_NAME + " - modified");
            assertEquals(1, History.getHistorySize());
        }
        // ... but nonetheless, it should be possible to undo the history without problems
        assertTrue(History.undo());
        assertEquals(0, History.getHistorySize());
    }

}
