# Simulador de Filas

**Disciplina:** Simulação e Métodos Analíticos  
**Atividade:** M6 — Simulação de Filas em Tandem

## 👥 Integrantes do Grupo

| Nome | 
|------|
| Gabriel Domingues |
| Fernando Gazzana |
| Vinícius Silva |


## Como Rodar

Compile:

```powershell
javac .\src\*.java
```

Execute informando o arquivo YAML:

```powershell
java -cp .\src Simulador model.yml
```

O arquivo `.yml` deve conter o modelo da simulacao e os numeros aleatorios, usando `seeds` com `rndnumbersPerSeed` ou usando `rndnumbers`.
