import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Memory {

    private byte[] memory = new byte[4096];

    public void write(short address, byte value) {
        memory[address] = value;
    }

    public byte read(short address) {
        return memory[address];
    }

    public void loadROMBytes(byte[] rom) {
        for (int i = 0; i < rom.length; i++) {
            write((short) (0x200 + i), rom[i]);
        }
    }

    public void loadROM(Path romPath) {
        byte[] romBytes;

        try {

            romBytes = Files.readAllBytes(romPath);
            loadROMBytes(romBytes);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
