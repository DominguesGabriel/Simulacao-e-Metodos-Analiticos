# Simulador de Filas em Tandem

**Disciplina:** Simulação e Métodos Analíticos  
**Atividade:** M6 — Simulação de Filas em Tandem

## 👥 Integrantes do Grupo

| Nome | 
|------|
| Gabriel Domingues |
| Fernando Gazzana |
| Vinícius Silva |

---

## 📋 Descrição

Simulador de eventos discretos para redes de filas em tandem, desenvolvido em Java. O simulador modela duas filas encadeadas onde clientes chegam do exterior à Fila 1 e, após atendimento, são 100% encaminhados para a Fila 2, saindo do sistema ao final.

O escalonador de eventos é implementado com `PriorityQueue<Evento>`, garantindo que os eventos sejam processados em ordem cronológica. Os números pseudoaleatórios são gerados pelo método **Gerador Linear Congruencial (LGC)**.

---

## 📁 Estrutura dos Arquivos

```
.
├── Evento.java       # Classe de evento (CHEGADA, SAIDA, PASSAGEM) com Comparable por tempo
├── Fila.java         # Classe da fila com todas as propriedades e métodos de controle
├── GeradorLgc.java   # Gerador de números pseudoaleatórios Linear Congruencial
└── Simulador.java    # Lógica principal da simulação (main)
```

---

## ⚙️ Pré-requisitos

- **Java JDK 8** ou superior instalado
- Verificar instalação:
  ```bash
  java -version
  javac -version
  ```

---

## 🚀 Como Compilar e Executar

### 1. Compilar todos os arquivos

Dentro da pasta com os `.java`:

```bash
javac *.java
```

### 2. Executar o simulador

**Usando os parâmetros padrão** (semente = 12345, 100.000 aleatórios):
```bash
java Simulador
```

**Informando uma semente personalizada:**
```bash
java Simulador 99999
```

**Informando semente e quantidade de aleatórios:**
```bash
java Simulador 12345 100000
```

---

## 🎛️ Configuração das Filas

Os parâmetros das filas estão definidos como constantes no topo de `Simulador.java` e podem ser alterados diretamente no código:

```java
// Fila 1 — G/G/2/3
static final int    F1_SERVERS  = 2;
static final int    F1_CAPACITY = 3;
static final double F1_MIN_ARR  = 1.0;   // chegadas entre 1..4
static final double F1_MAX_ARR  = 4.0;
static final double F1_MIN_SRV  = 3.0;   // atendimento entre 3..4
static final double F1_MAX_SRV  = 4.0;

// Fila 2 — G/G/1/5
static final int    F2_SERVERS  = 1;
static final int    F2_CAPACITY = 5;
static final double F2_MIN_SRV  = 2.0;   // atendimento entre 2..3
static final double F2_MAX_SRV  = 3.0;

// Condições iniciais
static final double PRIMEIRO_CLIENTE = 1.5;  // primeira chegada em t=1.5

