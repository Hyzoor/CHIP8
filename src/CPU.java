public class CPU {

    private final byte[] V = new byte[16];      //Registers V0 - VF (8-bit)
    private short I;                             //Register Index: stores memory addresses (16-bit)
    private byte delaytimer, soundtimer;        //Registers delay and sound timer (8-bit)
    private short PC;                            //Program Counter (16-bit)
    private byte SP;                            //Stack pointer (8-bit)
    private final short[] stack = new short[16];  //Stack: Array of 16 16-bits

    private final Memory memory;
    private final Display display;

    public CPU(Memory memory, Display display) {
        this.memory = memory;
        this.display = display;
    }


    public void cycle(){
        //fetch
        short instruction = fetch();

        //decode


        //execution
    }

    private short fetch(){
        byte msb = (byte)(memory.read(PC) & 0xFF);
        byte lsb = (byte)(memory.read((short)(PC+1)) & 0xFF);

        short instruction = (short)(msb << 8 | lsb);
        PC = (short)(PC + 2);

        return instruction;
    }

}
