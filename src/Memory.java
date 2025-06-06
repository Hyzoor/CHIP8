public class Memory {

    private byte[] memory = new byte[4096];

    public void write(short address, byte value){
        memory[address] = value;
    }

    public byte read(short address){
        return memory[address];
    }

}
