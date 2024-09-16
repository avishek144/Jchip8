import java.util.Random;

public abstract class Processor
{
    private static final int MAX_STACK_SIZE = 12;
    private static int pc = 0x200, sp = 0;
    static int index;
    private static int[] stack = new int[MAX_STACK_SIZE];
    private static int[] register = new int[16];
    private static int delay_timer = 0, sound_timer = 0;


    public static void main(String[] args)
    {
        var parent_frame = Screen.initialize();

        while (true) {
            try {
                var instruction_msb = Memory.read(pc++);
                var instruction_lsb = Memory.read(pc++);

                var instruction = instruction_msb * 0x100 + instruction_lsb;
                var x = instruction_msb % 0x10;
                var y = instruction_lsb / 0x10;
                var address = instruction % 0x1000;

                var opcode = instruction_msb / 0x10;

                switch (opcode) {
                    case 0x0 -> {
                        switch (address) {
                            case 0x0E0 -> Screen.clear();

                            case 0x0EE -> pc = pop();
                        
                            default -> throw new InvalidMemoryAddressException(address);
                        }
                    }

                    case 0x1 -> {
                        if (address > Memory.MAX_ADDRESS || address < Memory.STATRING_ADDRESS)
                            throw new InvalidMemoryAddressException(address);
                        else
                            pc = address;
                    }

                    case 0x2 -> {
                        push(pc);
                        pc = address;
                    }

                    case 0x3 -> {
                        if (register[x] == instruction_lsb)
                            pc += 2;
                        else
                            continue;
                    }

                    case 0x4 -> {
                        if (register[x] != instruction_lsb)
                            pc += 2;
                        else
                            continue;
                    }

                    case 0x5 -> {
                        if ((instruction_lsb % 0x10) == 0) {
                            if (register[x] == register[y])
                                pc += 2;
                            else
                                continue;
                        }
                        else {
                            throw new InvalidInstructionException(instruction, pc-2);
                        }
                    }

                    case 0x6 -> {
                        register[x] = instruction_lsb;
                    }

                    case 0x7 -> {
                        register[x] += instruction_lsb;
                    }

                    case 0x8 -> {
                        var n = instruction_lsb % 0x10;
                        switch (n) {
                            case 0x0 -> register[y] = register[x];

                            case 0x1 -> register[x] |= register[y];

                            case 0x2 -> register[x] &= register[y];

                            case 0x3 -> register[x] ^= register[y];

                            case 0x4 -> {
                                register[x] += register[y];
                                if (register[x] > 0xFF) {
                                    register[0xF] = 1;
                                    register[x] %= 0x100;
                                }
                                else
                                    register[0xF] = 0;
                            }

                            case 0x5 -> {
                                register[x] -= register[y];
                                if (register[x] > register[y])
                                    register[0xF] = 1;
                                else {
                                    register[0xF] = 0;
                                    register[x] %= 0x100;
                                }
                            }

                            case 0x6 -> {
                                register[0xF] = register[x] % 2;
                                register[x] /= 2;
                            }

                            case 0x7 -> {
                                register[x] = register[y] - register[x];
                                if (register[y] > register[x])
                                    register[0xF] = 1;
                                else {
                                    register[0xF] = 0;
                                    register[x] %= 0x100;
                                }
                            }

                            case 0xE -> {
                                if (register[x] > 0xEF)
                                    register[0xF] = 0x1;
                                else
                                    register[0xF] = 0x0;
                                register[x] = (register[x] * 2) % 0x100;
                            }
                        
                            default -> throw new InvalidInstructionException(instruction, pc-2);
                        }
                    }

                    case 0x9 -> {
                        if ((instruction_lsb % 0x10) == 0) {
                            if (register[x] != register[y])
                                pc += 2;
                            else
                                continue;
                        }
                        else
                            throw new InvalidInstructionException(instruction, pc-2);
                    }

                    case 0xA -> index = address;

                    case 0xB -> {
                        if (address > Memory.MAX_ADDRESS || address < Memory.STATRING_ADDRESS)
                            throw new InvalidMemoryAddressException(address);
                        else
                            pc = register[0x0] + address;
                    }

                    case 0xC -> register[x] = (new Random().nextInt()) & instruction_lsb;

                    case 0xD -> Screen.draw(register[x], register[y], index, instruction_lsb % 0x10);

                    case 0xE -> {
                        switch (instruction_lsb) {
                            case 0x9E -> {
                                if (Keypad.pressed(x))
                                    pc += 2;
                                else
                                    continue;
                            }

                            case 0xA1 -> {
                                if (!Keypad.pressed(x))
                                    pc += 2;
                                else
                                    continue;
                            }
                        
                            default -> throw new InvalidInstructionException(instruction, pc-2);
                        }
                    }

                    case 0xF -> {
                        switch (instruction_lsb) {
                            case 0x07 -> {
                                
                            }

                            case 0x0A -> {

                            }

                            case 0x15 -> delay_timer = register[x];

                            case 0x18 -> sound_timer = register[x];

                            case 0x1E -> index = (index + register[x]) % 0x1000;

                            case 0x29 -> index = x;

                            case 0x33 -> {

                            }

                            case 0x55 -> {
                                var start_address = index;
                                for (var i = 0; i < x; ++i, ++start_address)
                                    Memory.write(start_address, register[i]);
                            }

                            case 0x65 -> {
                                var start_address = index;
                                for (var i = 0; i < x; ++i, ++start_address)
                                    register[i] = Memory.read(start_address);
                            }
                                
                            default -> throw new InvalidInstructionException(instruction, pc-2);
                        }
                    }

                    default -> {

                    }
                }
            }
            catch (
                Chip8StackOverflow |
                Chip8StackUnderflow |
                InvalidInstructionException |
                InvalidMemoryAddressException invalid_data_exception
            ) {
                ExceptionDialog.show(parent_frame, invalid_data_exception);
            }
        }
    }

    private static int pop() throws Chip8StackUnderflow
    {
        if (sp == 0)
            throw new Chip8StackUnderflow();
        else
            return stack[--sp];
    }

    private static void push(int address) throws Chip8StackOverflow
    {
        if (sp == MAX_STACK_SIZE)
            throw new Chip8StackOverflow();
        else
            stack[sp++] = address;
    }
}
