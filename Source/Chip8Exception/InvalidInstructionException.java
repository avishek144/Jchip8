public class InvalidInstructionException extends Exception {
    private int instruction, address;

    InvalidInstructionException(int instruction, int address)
    {
        this.instruction = instruction;
        this.address = address;
    }

    @Override
    public String toString() {
        return String.format("Invalid instruction: %x at address %x", instruction, address);
    }
}
