
import java.util.*;

public class Simulador {
    static Map<String, Fila> filas = new HashMap<>();
    static PriorityQueue<Evento> escalonador = new PriorityQueue<>();
    static GeradorLgc gerador;
    static double tempoAtual = 0.0;
    static int maxAleatorios;

    public static void main(String[] args) {
        long semente = 51232312;
        maxAleatorios = 100_000;

        if (args.length >= 1) {
            semente = Long.parseLong(args[0]);
        }
        if (args.length >= 2) {
            maxAleatorios = Integer.parseInt(args[1]);
        }

        gerador = new GeradorLgc(semente);

        filas.put("Fila1", new Fila("Fila1", 1, Integer.MAX_VALUE, 2.0, 4.0, 1.0, 2.0));
        filas.put("Fila2", new Fila("Fila2", 2, 5, 0.0, 0.0, 4.0, 6.0));
        filas.put("Fila3", new Fila("Fila3", 2, 10, 0.0, 0.0, 5.0, 15.0));

        // Agenda a primeira chegada
        tempoAtual = 0.0;
        escalonador.add(new Evento(Evento.CHEGADA, 2.0, "Fila1"));

        // Loop Principal
        while (!escalonador.isEmpty() && gerador.getCount() < maxAleatorios) {
            Evento ev = escalonador.poll();

            acumulaTempo(ev.getTempo() - tempoAtual);
            tempoAtual = ev.getTempo();

            if (ev.getTipo() == Evento.CHEGADA) {
                chegada(ev);
            } else if (ev.getTipo() == Evento.SAIDA) {
                saida(ev);
            }
        }

        imprimirResultados();
    }

    static void acumulaTempo(double delta) {
        if (delta <= 0) {
            return;
        }

        for (Fila f : filas.values()) {
            f.acumulaTempo(delta);
        }
    }

    static void chegada(Evento ev) {
        Fila f = filas.get(ev.getNomeFila());

        if (ev.getNomeFila().equals("Fila1") && gerador.getCount() < maxAleatorios) {
            double proximaChegada = tempoAtual + gerador.intervalo(f.minArrival(), f.maxArrival());
            escalonador.add(new Evento(Evento.CHEGADA, proximaChegada, "Fila1"));
        }

        if (f.status() < f.capacity()) {
            f.in();
            if (f.status() <= f.servers()) {
                if (gerador.getCount() < maxAleatorios) {
                    double fimServico = tempoAtual + gerador.intervalo(f.minService(), f.maxService());
                    escalonador.add(new Evento(Evento.SAIDA, fimServico, f.getNomeFila()));
                }
            }
        } else {
            f.contaPerda();
        }
    }

    static String sortearProximoDestino(String origem) {
        double rnd = gerador.proximo();

        switch (origem) {
            case "Fila1":
                return (rnd <= 0.8) ? "Fila2" : "Fila3";

            case "Fila2":
                if (rnd <= 0.3) return "Fila1";
                if (rnd <= 0.8) return "Fila2";
                return null;

            case "Fila3":
                if (rnd <= 0.7) return "Fila2";
                return null;

            default:
                return null;
        }
    }

    static void saida(Evento ev) {
        Fila origem = filas.get(ev.getNomeFila());
        origem.out();

        if (origem.status() >= origem.servers()) {
            if (gerador.getCount() < maxAleatorios) {
                double tempoServico = tempoAtual + gerador.intervalo(origem.minService(), origem.maxService());
                escalonador.add(new Evento(Evento.SAIDA, tempoServico, origem.getNomeFila()));
            }
        }

        if (gerador.getCount() < maxAleatorios) {
            String nomeDestino = sortearProximoDestino(origem.getNomeFila());

            if (nomeDestino != null) {
                Fila destino = filas.get(nomeDestino);

                if (destino.status() < destino.capacity()) {
                    destino.in();
                    if (destino.status() <= destino.servers()) {
                        if (gerador.getCount() < maxAleatorios) {
                            double tempoServicoDest = tempoAtual + gerador.intervalo(destino.minService(), destino.maxService());
                            escalonador.add(new Evento(Evento.SAIDA, tempoServicoDest, destino.getNomeFila()));
                        }
                    }
                } else {
                    destino.contaPerda();
                }
            }
        }
    }

    static void imprimirResultados() {
        System.out.println("Tempo global da simulação: " + tempoAtual);
        System.out.println("Aleatórios utilizados: " + gerador.getCount());

        for (String nomeFila : filas.keySet()) {
            Fila f = filas.get(nomeFila);
            imprimirFila(nomeFila, f);
        }
    }

    static void imprimirFila(String nome, Fila fila) {
        Map<Integer, Double> times = fila.getTimesMap();
        double totalTempoFila = 0;

        for (double t : times.values()) {
            totalTempoFila += t;
        }

        System.out.println("Fila: " + nome);
        System.out.printf("%-10s %-20s %-15s%n", "Estado", "Tempo", "Probabilidade");

        List<Integer> estados = new ArrayList<>(times.keySet());
        Collections.sort(estados);

        for (Integer estado : estados) {
            double tempo = times.get(estado);
            double prob = tempo / totalTempoFila;
            System.out.printf("%-10d %-20.4f %-15.6f%n", estado, tempo, prob);
        }
        System.out.println("Perdas: " + fila.loss());
    }
}
