package kanga.kcae.xchg.autocad;

public interface ShapeInstructionHandler {
    void handle(ActivateDraw instruction);
    void handle(DeactivateDraw instruction);
    void handle(DivideVectorLength instruction);
    void handle(DrawArc instruction);
    void handle(DrawDirectionalLine instruction);
    void handle(DrawSubshape instruction);
    void handle(MultiplyVectorLength instruction);
    void handle(PopLocation instruction);
    void handle(PushLocation instruction);
    void handle(RelativeMoveTo instruction);
}
