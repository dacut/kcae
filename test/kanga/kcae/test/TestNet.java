package kanga.kcae.test;

import kanga.kcae.object.Net;
import kanga.kcae.object.Port;
import kanga.kcae.object.SignalDirection;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestNet {
    @Test
    public void testName() {
        Net n = new Net("a name");
        assertEquals(n.getName(), "a name");
        n.setName("another name");
        assertEquals(n.getName(), "another name");
    }

    @Test
    public void testConnections() {
        Net n = new Net("net1");
        Port p1 = new Port("port1", SignalDirection.INPUT, n);
        Port p2 = new Port("port2", SignalDirection.OUTPUT, n);
        n.addConnection(p1);
        
        assertTrue(n.getConnections().contains(p1));

        n.removeConnection(p1);
        assertFalse(n.getConnections().contains(p1));

        n.addConnection(p1);
        n.addConnection(p2);
        assertTrue(n.isConnectedTo(p1));
        assertTrue(n.isConnectedTo(p2));
        n.removeConnection(p2);
        assertTrue(n.isConnectedTo(p1));
        assertFalse(n.isConnectedTo(p2));
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testConnectionsUnmodifiable() {
        Net n = new Net("net1");
        Port p1 = new Port("port1", SignalDirection.INPUT, n);
        n.getConnections().add(p1);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testComparisons() {
        Net n1a = new Net("net1");
        Net n1b = new Net("net1");

        assertFalse(n1a.equals(null));
        assertFalse(n1b.equals(null));
        assertTrue(n1a.equals(n1a));
        assertTrue(n1a.equals(n1b));
        assertTrue(n1b.equals(n1a));
        assertTrue(n1b.equals(n1b));
        assertEquals(n1a.hashCode(), n1b.hashCode());
        assertEquals(n1a.compareTo(n1a), 0);
        assertEquals(n1a.compareTo(n1b), 0);
        assertEquals(n1b.compareTo(n1a), 0);
        assertEquals(n1b.compareTo(n1b), 0);

        Net n2 = new Net("net2");
        assertFalse(n1a.equals(n2));
        assertFalse(n1a.hashCode() == n2.hashCode());
        assertTrue(n1a.compareTo(n2) < 0);
        assertTrue(n2.compareTo(n1a) > 0);

        Net n1c = new Net("net1") {
            @Override
            public int hashCode() {
                return super.hashCode();
            }
        };
        assertFalse(n1a.equals(n1c));
        assertFalse(n1c.equals(n1a));
        
        Port p1a = new Port("port1", SignalDirection.INPUT, n1a);
        Port p1b = new Port("port1", SignalDirection.INPUT, n1b);
        
        n1a.addConnection(p1a);
        assertTrue(n1a.equals(n1b));
        assertTrue(n1a.compareTo(n1b) == 0);
        assertTrue(((Comparable) n1a).compareTo((Object) n1b) == 0);

        n1b.addConnection(p1b);
        assertTrue(n1a.equals(n1b));
        assertTrue(n1a.compareTo(n1b) == 0);
    }

    @Test
    public void testStringForm() {
        Net n1a = new Net("net1");

        assertEquals(n1a.toString(), "Net[name=net1]");
    }
}