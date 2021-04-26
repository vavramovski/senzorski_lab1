
public class Series {
    public double NA;
    public double IA1;
    public double IA2;

    public Series(double NA, double IA1, double IA2) {
        this.NA = NA;
        this.IA1 = IA1;
        this.IA2 = IA2;
    }

    public double getByString(String name) {
        if (name.equals("NA"))
            return NA;
        if (name.equals("IA1"))
            return IA1;
        if (name.equals("IA2"))
            return IA2;
        return 0.0;
    }
}
