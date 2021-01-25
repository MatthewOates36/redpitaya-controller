import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CommandFPGA {

    public void setMemory(int offset, int value) {
        try {
            Runtime.getRuntime().exec(String.format("monitor %s %s", 0x42000000 + offset, value));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setMemory0(int value) {
        setMemory(0x0, value);
    }

    public void setMemory1(int value) {
        setMemory(0x8, value);
    }

    public void setMemory2(int value) {
        setMemory(0x1000, value);
    }

    public void setMemory3(int value) {
        setMemory(0x1008, value);
    }

    public int getMemory(int offset) {
        try {
            Process process = Runtime.getRuntime().exec(String.format("monitor %s", 0x42000000 + offset));

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = "";
            while ((line = reader.readLine()) != null) {
                try {
                    return Long.decode(line).intValue();
                } catch (Exception e) {}
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
