
import java.util.PriorityQueue;

public class Simulador {

    static final int F1_SERVERS = 2;
    static final int F1_CAPACITY = 3;
    static final double F1_MIN_ARR = 1.0;
    static final double F1_MAX_ARR = 4.0;
    static final double F1_MIN_SRV = 3.0;
    static final double F1_MAX_SRV = 4.0;

    static final int F2_SERVERS = 1;
    static final int F2_CAPACITY = 5;
    static final double F2_MIN_ARR = 0.0;
    static final double F2_MAX_ARR = 0.0;
    static final double F2_MIN_SRV = 2.0;
    static final double F2_MAX_SRV = 3.0;

    static final double PRIMEIRO_CLIENTE = 1.5;

    static Fila fila1, fila2;
    static PriorityQueue<Evento> escalonador = new PriorityQueue<>();
    static GeradorLgc gerador;
    static double tempoAtual = 0.0;
    static int maxAleatorios;

    public static void main(String[] args) {
        long semente = 12345L;
        maxAleatorios = 100_000;

        if (args.length >= 1) {
            semente = Long.parseLong(args[0]);
        }
        if (args.length >= 2) {
            maxAleatorios = Integer.parseInt(args[1]);
        }

        gerador = new GeradorLgc(semente);

        fila1 = new Fila(F1_SERVERS, F1_CAPACITY,
                F1_MIN_ARR, F1_MAX_ARR,
                F1_MIN_SRV, F1_MAX_SRV);

        fila2 = new Fila(F2_SERVERS, F2_CAPACITY,
                F2_MIN_ARR, F2_MAX_ARR,
                F2_MIN_SRV, F2_MAX_SRV);

        // Agenda a primeira chegada
        escalonador.add(new Evento(Evento.CHEGADA, PRIMEIRO_CLIENTE));

        // Loop principal
        while (gerador.getCount() < maxAleatorios && !escalonador.isEmpty()) {
            Evento ev = escalonador.poll();

            // Acumula tempo em ambas as filas antes de processar evento
            acumulaTempo(ev.getTempo() - tempoAtual);
            tempoAtual = ev.getTempo();

            if (ev.getTipo() == Evento.CHEGADA) {
                chegada(ev); 
            }else if (ev.getTipo() == Evento.SAIDA) {
                saida(ev); 
            }else if (ev.getTipo() == Evento.PASSAGEM) {
                passagem(ev);
            }
        }

        imprimirResultados();
    }

    static void acumulaTempo(double delta) {
        if (delta <= 0) {
            return;
        }
        fila1.acumulaTempo(delta);
        fila2.acumulaTempo(delta);
    }

    static void chegada(Evento ev) {
        // Agenda a próxima chegada (usa 1 aleatório)
        if (gerador.getCount() < maxAleatorios) {
            double proxChegada = tempoAtual
                    + gerador.intervalo(fila1.minArrival(), fila1.maxArrival());
            escalonador.add(new Evento(Evento.CHEGADA, proxChegada));
        }

        if (fila1.status() < fila1.capacity()) {
            fila1.in();
            // Se há servidor livre, agenda atendimento (usa 1 aleatório)
            if (fila1.status() <= fila1.servers()) {
                if (gerador.getCount() < maxAleatorios) {
                    double fimAtendimento = tempoAtual
                            + gerador.intervalo(fila1.minService(), fila1.maxService());
                    // Ao sair da fila1, o cliente vai para fila2 (PASSAGEM)
                    escalonador.add(new Evento(Evento.PASSAGEM, fimAtendimento));
                }
            }
        } else {
            fila1.contaPerda();
        }
    }

    static void passagem(Evento ev) {
        fila1.out();

        // Libera servidor na fila1: atende próximo se houver fila
        if (fila1.status() >= fila1.servers()) {
            if (gerador.getCount() < maxAleatorios) {
                double fimAtendimento = tempoAtual
                        + gerador.intervalo(fila1.minService(), fila1.maxService());
                escalonador.add(new Evento(Evento.PASSAGEM, fimAtendimento));
            }
        }

        // Tenta entrar na Fila 2
        if (fila2.status() < fila2.capacity()) {
            fila2.in();
            if (fila2.status() <= fila2.servers()) {
                if (gerador.getCount() < maxAleatorios) {
                    double fimAtendimento = tempoAtual
                            + gerador.intervalo(fila2.minService(), fila2.maxService());
                    escalonador.add(new Evento(Evento.SAIDA, fimAtendimento));
                }
            }
        } else {
            fila2.contaPerda();
        }
    }

    static void saida(Evento ev) {
        fila2.out();

        // Libera servidor: atende próximo se houver fila
        if (fila2.status() >= fila2.servers()) {
            if (gerador.getCount() < maxAleatorios) {
                double fimAtendimento = tempoAtual
                        + gerador.intervalo(fila2.minService(), fila2.maxService());
                escalonador.add(new Evento(Evento.SAIDA, fimAtendimento));
            }
        }
    }

    static void imprimirResultados() {
        System.out.println("=".repeat(60));
        System.out.println("   SIMULADOR DE FILAS EM TANDEM");
        System.out.println("=".repeat(60));
        System.out.printf("Aleatórios utilizados : %d%n", gerador.getCount());
        System.out.printf("Tempo global da simulação: %.4f%n%n", tempoAtual);

        imprimirFila("FILA 1 [G/G/" + F1_SERVERS + "/" + F1_CAPACITY + "]", fila1);
        imprimirFila("FILA 2 [G/G/" + F2_SERVERS + "/" + F2_CAPACITY + "]", fila2);
    }

    static void imprimirFila(String nome, Fila fila) {
        double[] times = fila.times();
        double total = 0;
        for (double t : times) {
            total += t;
        }

        System.out.println("-".repeat(60));
        System.out.println("  " + nome);
        System.out.println("-".repeat(60));
        System.out.printf("  Perdas: %d%n", fila.loss());
        System.out.printf("  %-8s %-18s %-12s%n", "Estado", "Tempo Acumulado", "Probabilidade");
        System.out.printf("  %-8s %-18s %-12s%n", "------", "---------------", "------------");

        for (int i = 0; i <= fila.capacity(); i++) {
            double prob = (total > 0) ? times[i] / total : 0.0;
            System.out.printf("  %-8d %-18.4f %-12.6f%n", i, times[i], prob);
        }
        System.out.printf("  %s%n  Total: %.4f%n%n", "-".repeat(40), total);
    }
}
