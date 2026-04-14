
public class GeradorLgc {

    private long x;
    private final long a = 1664525L;
    private final long c = 1013904223L;
    private final double m = Math.pow(2, 32);
    private int count = 0;

    public GeradorLgc(long semente) {
        this.x = semente;
    }

    /**
     * Retorna o próximo número no intervalo [0,1) e incrementa o contador.
     */
    public double proximo() {
        x = (a * x + c) % (long) m;
        count++;
        return x / m;
    }

    /**
     * Mapeia o próximo aleatório para o intervalo [min, max].
     */
    public double intervalo(double min, double max) {
        return min + proximo() * (max - min);
    }

    public int getCount() {
        return count;
    }
}
