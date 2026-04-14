
public class Fila {

    private int servers;
    private int capacity;
    private double minArrival;
    private double maxArrival;
    private double minService;
    private double maxService;
    private int customers;
    private int loss;
    private double[] times;

    public Fila(int servers, int capacity,
            double minArrival, double maxArrival,
            double minService, double maxService) {
        this.servers = servers;
        this.capacity = capacity;
        this.minArrival = minArrival;
        this.maxArrival = maxArrival;
        this.minService = minService;
        this.maxService = maxService;
        this.customers = 0;
        this.loss = 0;
        this.times = new double[capacity + 1];
    }

    public int status() {
        return customers;
    }

    public int capacity() {
        return capacity;
    }

    public int servers() {
        return servers;
    }

    public int loss() {
        return loss;
    }

    public double[] times() {
        return times;
    }

    public double minArrival() {
        return minArrival;
    }

    public double maxArrival() {
        return maxArrival;
    }

    public double minService() {
        return minService;
    }

    public double maxService() {
        return maxService;
    }

    public void contaPerda() {
        loss++;
    }

    public void in() {
        customers++;
    }

    public void out() {
        customers--;
    }

    public void acumulaTempo(double delta) {
        if (customers <= capacity) {
            times[customers] += delta;
        }
    }
}
