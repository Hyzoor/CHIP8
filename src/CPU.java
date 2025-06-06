import java.util.Random;

public class CPU {

    private final byte[] registersV = new byte[16];         //Registers V0 - VF (8-bit)
    private short registerIndex;                            //Register Index: stores memory addresses (16-bit)
    private byte delaytimer, soundtimer;                    //Registers delay and sound timer (8-bit)
    private short PC;                                       //Program Counter (16-bit)
    private byte SP;                                        //Stack pointer (8-bit)
    private final short[] stack = new short[16];            //Stack: Array of 16 16-bits

    private final Memory memory;
    private final Display display;
    Random random;

    public CPU(Memory memory, Display display) {
        this.memory = memory;
        this.display = display;
        this.random = new Random();
        reset();
    }

    private void add2PC(){
        PC = (short)(PC + 2);
    }

    public void cycle(){

        short instruction = fetch();
        decodeExecution(instruction);

    }

    private void reset(){
        PC = (short)(0x200);
        SP = 0;
        registerIndex = 0;
        display.clear();
    }

    private byte getnibble(short instruction, int i){

        return switch(i){

            case 1 -> (byte) ((instruction & 0xF000) >> 12);
            case 2 -> (byte) ((instruction & 0x0F00) >> 8);
            case 3 -> (byte) ((instruction & 0x00F0) >> 4);
            case 4 -> (byte) (instruction & 0x000F);
            default -> throw new RuntimeException("Nibble must be 1-4");

        };

    }

    private short fetch(){
        byte msb = (byte)(memory.read(PC) & 0xFF);
        byte lsb = (byte)(memory.read((short)(PC+1)) & 0xFF);

        short instruction = (short)(msb << 8 | lsb);
        add2PC();

        return instruction;
    }

    private void decodeExecution(short instruction){

        byte x = getnibble(instruction, 2);
        byte y = getnibble(instruction, 3);
        byte n = getnibble(instruction, 4);
        byte nn = (byte) ((getnibble(instruction,3)) << 4 | (getnibble(instruction,4)));
        short nnn = (short) (getnibble(instruction, 2) << 8 | nn);

        byte opcode = (byte) (getnibble(instruction, 1));

        switch(opcode){

            case 0x0:
                switch(instruction){
                    // 00E0 CLS - Clear the display
                    case 0x00E0: display.clear(); break;

                    // 00EE RET - Return from a subroutine
                    case 0x00EE: PC = stack[SP--]; break;

                    // 0nnn SYS address - Jump to a machine code routine at nnn
                    default: //Not implemented
                }
                break;

            // 1nnn JP address - PC = nnn
            case 0x1: PC = nnn; break;

            // 2nnn CALL address - Call subroutine at nnn
            case 0x2: stack[++SP] = PC; PC = nnn; break;

            // 3xnn SE Vx, byte - Skip next instruction if Vx = nn;
            case 0x3: if(registersV[x] == nn) add2PC(); break;

            // 4xnn SNE Vx, byte - Skip next instruction if Vx != nn
            case 0x4: if(registersV[x] != nn) add2PC(); break;

            // 5xy0 SE Vx, Vy - SKip next instruction if Vx == Vy
            case 0x5: if(registersV[x] == registersV[y]) add2PC(); break;

            // 6xnn LD Vx, byte - Set Vx = nn
            case 0x6: registersV[x] = nn; break;

            // 7xnn ADD Vx, byte - Set Vx = Vx + nn
            case 0x7: registersV[x] += nn; break;

            case 0x8:
                switch(instruction){

                }
                break;

            // 9xy0 SNE Vx, Vy - Skip next instruction if Vx != Vy
            case 0x9: if(registersV[x] != registersV[y]) add2PC(); break;

            // Annn LD I, nnn - Set I = nnn
            case 0xA: registerIndex = nnn; break;

            // Bnnn JP V0, address - Jump to nnn + V0
            case 0xB: PC = (short)(nnn + registersV[0x0]); break;

            // Cxkk RND Vx, byte - Set Vx = random byte AND nn
            case 0xC: registersV[x] = (byte)(random.nextInt(256) & (nn & 0xFF)); break;

            // Dxyn DRW Vx, Vy, nibble - Display n-byte sprite starting at mem[Index] set VF = collision
            case 0xD:
                int xpos = registersV[x] & 0xFF;
                int ypos = registersV[y] & 0xFF;
                registersV[0xF] = 0;

                for(int i=0; i < n; i++){
                    byte spriteByte = memory.read((short)(registerIndex + i));

                    for(int j=0; j < 8; j++){

                        int xPixel = (xpos + j) % 64;
                        int yPixel = (ypos + i) % 32;
                        boolean spritePixel = (spriteByte >> (7 - j) & 0x1) == 1;
                        boolean currentPixel = display.getPixel(xPixel, yPixel);

                        if(spritePixel && currentPixel){
                            registersV[0xF] = 1; //Collision
                        }

                        display.setPixel(xPixel, yPixel, spritePixel ^ currentPixel);
                    }
                }
                display.repaint();
                break;

        }
    }


}
