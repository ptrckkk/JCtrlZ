package test;

import de.ptrckkk.jctrlz.Undoable;

/**
 * The only purpose of this class is to create mock objects which can be used to check if setting null and other values,
 * which produce the same hash code, are still treated correctly.
 *
 * @author ptrckkk
 */
@Undoable(false)
public class MockHashCodeClass {

    private Integer value;

    public MockHashCodeClass(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        return (value == null) ? 0 : value;
    }

}
