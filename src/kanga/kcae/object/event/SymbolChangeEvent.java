package kanga.kcae.object.event;

import kanga.kcae.object.Symbol;

public class SymbolChangeEvent {
    public SymbolChangeEvent(final Symbol symbol) {
        this.symbol = symbol;
    }
    
    public Symbol getSymbol() {
        return this.symbol;
    }
    
    private final Symbol symbol;
}
