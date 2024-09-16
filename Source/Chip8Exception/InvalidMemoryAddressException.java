class InvalidMemoryAddressException extends Exception
{
    private int address;

    InvalidMemoryAddressException(int address)
    {
        this.address = address;
    }

    @Override
    public String toString()
    {
        return String.format("%x should not be used!", address);
    }
}
