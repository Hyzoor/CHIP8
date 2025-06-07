import java.util.Random;

public class CPU {

    private final char[] registersV = new char[16];         //Registers V0 - VF (8-bit)
    private final char[] stack = new char[16];            //Stack: Array of 16 16-bits
    private final Memory memory;
    private final Display display;
    private final Keypad keypad;
    private final Random random;
    private char registerIndex;                            //Register Index: stores memory addresses (16-bit)
    private char delaytimer, soundtimer;                    //Registers delay and sound timer (8-bit)
    private char PC;                                       //Program Counter (16-bit)
    private char SP;                                        //Stack pointer (8-bit)

    public CPU(Memory memory, Display display, Keypad keypad) {
        this.memory = memory;
        this.display = display;
        this.keypad = keypad;
        this.random = new Random();
        PC = 0x200;
        SP = 0;
        registerIndex = 0;
        display.clear();
    }

    public void cycle() {

        char instruction = fetch();
        PC += 2;
        decodeExecution(instruction);

        if (delaytimer > 0) --delaytimer;
        if (soundtimer > 0) --soundtimer;

    }

    private char getNibble(char instruction, int i) {

        return (char) switch (i) {
            case 1 -> ((instruction & 0xF000) >> 12);
            case 2 -> ((instruction & 0x0F00) >> 8);
            case 3 -> ((instruction & 0x00F0) >> 4);
            case 4 -> (instruction & 0x000F);
            default -> throw new RuntimeException("Nibble must be 1-4");

        };

    }

    private char fetch() {
        char msb = memory.read(PC);
        char lsb = memory.read(PC + 1);
        return (char) (msb << 8 | lsb);
    }

    private void decodeExecution(char instruction) {
        char x = getNibble(instruction, 2);
        char y = getNibble(instruction, 3);
        char n = getNibble(instruction, 4);
        char nn = (char) (getNibble(instruction, 3) << 4 | n);
        char nnn = (char) (getNibble(instruction, 2) << 8 | nn);

        char opcode = (getNibble(instruction, 1));

        switch (opcode) {

            case 0x0:
                switch (instruction) {
                    // 00E0 CLS - Clear the display
                    case 0x00E0:
                        display.clear();
                        break;

                    // 00EE RET - Return from a subroutine
                    case 0x00EE:
                        PC = stack[SP--];
                        break;

                    // 0nnn SYS address - Jump to a machine code routine at nnn
                    default: //Not implemented
                }
                break;

            // 1nnn JP address - PC = nnn
            case 0x1:
                PC = nnn;
                break;

            // 2nnn CALL address - Call subroutine at nnn
            case 0x2:
                stack[++SP] = PC;
                PC = nnn;
                break;

            // 3xnn SE Vx, byte - Skip next instruction if Vx = nn;
            case 0x3:
                if (registersV[x] == nn) PC+=2;
                break;

            // 4xnn SNE Vx, byte - Skip next instruction if Vx != nn
            case 0x4:
                if (registersV[x] != nn) PC+=2;
                break;

            // 5xy0 SE Vx, Vy - SKip next instruction if Vx == Vy
            case 0x5:
                if (registersV[x] == registersV[y]) PC+=2;
                break;

            // 6xnn LD Vx, byte - Set Vx = nn
            case 0x6:
                registersV[x] = nn;
                break;

            // 7xnn ADD Vx, byte - Set Vx = Vx + nn
            case 0x7:
                char res = (char) ((registersV[x] + nn ) & 0x00FF);
                registersV[x] = res;
                break;

            case 0x8:
                switch (n) {
                    // 8xy0 LD Vx, Vy - Set Vx = Vy
                    case 0x0:
                        registersV[x] = registersV[y];
                        break;
                    case 0x1:
                        registersV[x] = (char) (registersV[y] | registersV[x]);
                        break;
                    case 0x2:
                        registersV[x] = (char) (registersV[y] & registersV[x]);
                        break;
                    case 0x3:
                        registersV[x] = (char) (registersV[y] ^ registersV[x]);
                        break;
                    case 0x4:
                        char vx = (char) (registersV[x] + registersV[y]);
                        if ((vx & 0xFF00) > 0){
                            registersV[0x0F] = 0x01; vx &= 0x00FF;
                        }
                        else registersV[0x0F] = 0;
                        registersV[x] = vx;
                        break;
                    case 0x5:
                        char vxsub = (char) (registersV[x] - registersV[y]);
                        if (registersV[x] > registersV[y]){
                            registersV[0x0F] = 0x01;
                        }
                        else{
                            registersV[0x0F] = 0;
                            vxsub &= 0x00FF;
                        }
                        registersV[x] = vxsub;
                        break;
                    case 0x6:
                        registersV[0xF] = (char) (registersV[x] & 0x01);
                        registersV[x] = (char) ((registersV[x] & 0xFF) >> 1);
                        break;
                    case 0x7:
                        char vxsubn = (char) (registersV[y] - registersV[x]);
                        if (registersV[x] > registersV[y]){
                            registersV[0x0F] = 0x00;
                            vxsubn &= 0x00FF;
                        }
                        else{
                            registersV[0x0F] = 0x01;
                        }
                        registersV[x] = vxsubn;
                        break;
                    case 0xE:
                        registersV[0xF] = (char) (registersV[x] & 0x80);
                        registersV[x] = (char) ((registersV[x]  << 1) & 0x00FF);
                        break;
                }
                break;

            // 9xy0 SNE Vx, Vy - Skip next instruction if Vx != Vy
            case 0x9:
                if (registersV[x] != registersV[y]) PC+=2;
                break;

            // Annn LD I, nnn - Set I = nnn
            case 0xA:
                registerIndex = nnn;
                break;

            // Bnnn JP V0, address - Jump to nnn + V0
            case 0xB:
                PC = (char) (nnn + registersV[0x0]);
                break;

            // Cxkk RND Vx, byte - Set Vx = random byte AND nn
            case 0xC:
                registersV[x] = (char) (random.nextInt(256) & (nn & 0xFF));
                break;

            // Dxyn DRW Vx, Vy, nibble - Display n-byte sprite starting at mem[Index] set VF = collision
            case 0xD:
                int xpos = (registersV[x] & 0xFF) % 64;
                int ypos = (registersV[y] & 0xFF) % 32;
                registersV[0xF] = 0;

                for (int i = 0; i < n; i++) {
                    int spriteByte = (memory.read((char) (registerIndex + i)) & 0xFF);

                    int yPixel = (ypos + i);
                    if(yPixel >= 32) break;

                    for (int j = 0; j < 8; j++) {

                        int xPixel = (xpos + j);
                        if(xPixel >= 64) break;

                        boolean spritePixel = ((spriteByte >> (7 - j)) & 0x1) == 1;
                        boolean screenPixel = display.getPixel(xPixel, yPixel);

                        if(spritePixel && screenPixel){
                            display.setPixel(xPixel, yPixel, false);
                            registersV[0xF] = 1;
                        }
                        else if(spritePixel){
                            display.setPixel(xPixel, yPixel, true);
                        }
                    }
                }
                display.repaint();
                break;
            case 0xE:
                switch (nn) {
                    case 0x9E:
                        if (keypad.isKeyPressed(registersV[x] & 0xF)) PC+=2;
                        break;
                    case 0xA1:
                        if (!keypad.isKeyPressed(registersV[x] & 0xF)) PC+=2;
                        break;
                }
                break;
            case 0xF:
                switch (nn) {
                    case 0x07:
                        registersV[x] = delaytimer;
                        break;
                    case 0x0A:
                        boolean keyPressed = false;
                        for (int key = 0; key < 16; key++) {
                            if (keypad.isKeyPressed(key)) {
                                registersV[x] = (char) key;
                                keyPressed = true;
                                break;
                            }
                        }
                        if (!keyPressed) {
                            PC = (char) (PC - 2);
                        }
                        break;
                    case 0x15:
                        delaytimer = registersV[x];
                        break;
                    case 0x18:
                        soundtimer = registersV[x];
                        break;
                    case 0x1E:
                        registerIndex += registersV[x];
                        break;
                    case 0x29:
                        registerIndex = (char) (0x50 + (registersV[x] & 0xFF) * 5);
                        ;
                    case 0x33:
                        int decimalValue = registersV[x];
                        memory.write((char) (registerIndex + 2), (char) (decimalValue % 10));
                        decimalValue = decimalValue / 10;
                        memory.write((char) (registerIndex + 1), (char) (decimalValue % 10));
                        decimalValue = decimalValue / 10;
                        memory.write((registerIndex), (char) (decimalValue % 10));
                        break;
                    case 0x55:
                        for (int i = 0; i < registersV.length; i++) {
                            memory.write((char) (registerIndex + i), registersV[i]);
                        }
                        break;
                    case 0x65:
                        for (int i = 0; i < registersV.length; i++) {
                            registersV[i] = memory.read((char) (registerIndex + i));
                        }
                        break;
                }
        }
    }
}
