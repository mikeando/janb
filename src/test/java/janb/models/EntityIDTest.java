package janb.models;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by michaelanderson on 22/04/2015.
 */
public class EntityIDTest {
    @Test
    public void testParent() throws Exception {
        EntityID id = EntityID.fromComponents("a","b","c");
        assertThat(id.parent(), is(EntityID.fromComponents("a","b")));
    }

    @Test
    public void testParentReturnsEmptyForEmpty() throws Exception {
        EntityID id = EntityID.fromComponents();
        assertThat(id.parent(), is(EntityID.fromComponents()));
    }
}