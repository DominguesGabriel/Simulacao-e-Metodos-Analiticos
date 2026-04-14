
public class Evento implements Comparable<Evento> {

    public static final int CHEGADA = 0;
    public static final int SAIDA = 1;
    public static final int PASSAGEM = 2;

    private int tipo;
    private double tempo;

    public Evento(int tipo, double tempo) {
        this.tipo = tipo;
        this.tempo = tempo;
    }

    public int getTipo() {
        return tipo;
    }

    public double getTempo() {
        return tempo;
    }

    @Override
    public int compareTo(Evento outro) {
        return Double.compare(this.tempo, outro.tempo);
    }

    @Override
    public String toString() {
        String[] nomes = {"CHEGADA", "SAIDA", "PASSAGEM"};
        return String.format("Evento[%s, t=%.4f]", nomes[tipo], tempo);
    }
}
