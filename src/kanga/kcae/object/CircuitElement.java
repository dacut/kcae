package kanga.kcae.object;

import java.util.Set;

public interface CircuitElement {
    public void getNetsInto(Set<Net> nets);
    public String getName();
    public void setName(String name);
}