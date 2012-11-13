package kanga.kcae.xchg.autocad;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import static java.util.Collections.unmodifiableList;

public class AutoCADShape implements Serializable {
    public AutoCADShape(String shapeName, List<ShapeInstruction> instructions) {
        this(shapeName, instructions, false);
    }
    
    AutoCADShape(
        String shapeName,
        List<ShapeInstruction> instructions,
        boolean takeOwnership)
    {
        this.shapeName = shapeName;
        this.instructions = (takeOwnership ?
                             instructions :
                             new ArrayList<ShapeInstruction>(instructions));
    }
    
    public String getShapeName() {
        return this.shapeName;
    }
    
    public List<ShapeInstruction> getInstructions() {
        return unmodifiableList(this.instructions);
    }
    
    private final String shapeName;
    private final List<ShapeInstruction> instructions;
    private static final long serialVersionUID = 1L;
}
