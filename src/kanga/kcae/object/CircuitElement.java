package kanga.kcae.object;

import java.io.Serializable;
import java.util.Set;

public interface CircuitElement
    extends Comparable<CircuitElement>, Serializable
{
    public void getNetsInto(Set<Net> nets);
    public String getName();
    public void setName(String name);
}
