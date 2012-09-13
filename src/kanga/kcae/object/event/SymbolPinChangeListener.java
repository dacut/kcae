package kanga.kcae.object.event;

import java.util.Map;

import kanga.kcae.object.Pin;
import kanga.kcae.object.Symbol;

public interface SymbolPinChangeListener {
    public void pinAdded(Symbol symbol, Pin pin);
    public void pinRemoved(Symbol symbol, Pin pin);
    public void pinChanged(
        Symbol symbol,
        Pin pin,
        Map<String, PropertyChangeEvent<?>> properties);
}
