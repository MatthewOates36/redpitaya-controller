public class FPGA {

    static {
        System.loadLibrary("fpganative");
    }

    public native void setMemory(int base, int offset, long value);

    public void setMemory0(long value) {
        setMemory(0x42000000, 0x0, value);
    }

    public void setMemory1(long value) {
        setMemory(0x42000000, 0x8, value);
    }

    public void setMemory2(long value) {
        setMemory(0x42010000, 0x0, value);
    }

    public void setMemory3(long value) {
        setMemory(0x42010000, 0x0, value);
    }

    public native long getMemory(int base, int offset);

    public long getMemory0() {
        return getMemory(0x42000000, 0x0);
    }

    public long getMemory1() {
        return getMemory(0x42000000, 0x8);
    }

    public long getMemory2() {
        return getMemory(0x42010000, 0x0);
    }

    public long getMemory3() {
        return getMemory(0x42010000, 0x8);
    }
}
