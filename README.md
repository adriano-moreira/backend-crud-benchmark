# backend-crud-benchmark
Sim, mais um benchmark

## Motivação/Objetivo:
- Em uma api rest com interações com banco de dados postgres e redis 
- Medir ganho real ao utilizar programação reativa com quarkus e Spring
- Medir estado atual do suporte a virtual treads do Quarkus e Spring
- Pretendo atualizar/executar esse benchmark regularmente

## Minha interpretação atual dos resultados:
TODO: ...

TODO: documentar como executar os benchmark, explicando a estrutura do projeto

## Requisitos para executar os testes
- Linux/Bash
- Docker Swarm

## Teste
Para o test será implementado uma api rest semelhante à [rinha-de-backend-2023-q3](https://github.com/zanfranceschi/rinha-de-backend-2023-q3/blob/main/INSTRUCOES.md), com os seguintes endpoints:
- `POST /pessoas`
- `GET  /pessoas/[:id]`
- `GET  /pessoas/[:id]/cache`

## Meu teste minhas regras
- O banco de dados será [Postgres](https://www.postgresql.org/)
- Cada aplicação tera um limite maxima de 16 conexões permitidas
- O testes de carga serão com [k6](https://k6.io/)
- Aplicação será executada via docker swarm e terá cotas de cpu=2, men=512MB
- O Teste é executado com 1, 20 e 60 VUs por 45 segundos cada endpoint
- Aplicações Java ficão mais veloz após aquecidas, existe um pequeno aquecimento de 1000 requests em cada endpoint antes do teste de carga real, o mesmo para todas


### Por que testar com 1, 20 e 60 VUs(Virtual Users) ?
- 1 VU - pouca carga concorrência, sobra recurso de CPU
- 20 VUs - concorrência média, consome todo recurso de CPU
- 60 VUs - concorrência Alta

## Links relevantes
- https://quarkus.io/guides/virtual-threads

## Videos interessantes sobre o assunto interessantes
- Excelente apresentação sobre o Reactive Programming vs Virtual Threads no java

[![Java on YouTube](http://img.youtube.com/vi/zPhkg8dYysY/0.jpg)](https://www.youtube.com/watch?v=zPhkg8dYysY "Are Virtual Threads Going to Make Reactive Programming Irrelevant?")

- Excelente explicação sobre Spring Reativo/Web Flux, concordo 100% com a opinião apresentada

[![Trilha Sênior on YouTube](http://img.youtube.com/vi/sXiMI8mitwg/0.jpg)](https://www.youtube.com/watch?v=sXiMI8mitwg "Diga Adeus ao Spring WebFlux!")

- Melhor explicação que ja vi sobre JavaScript/Event Loop

[![Lydia Hallie on YouTube](http://img.youtube.com/vi/eiC58R16hb8/0.jpg)](https://www.youtube.com/watch?v=eiC58R16hb8 "JavaScript Visualized - Event Loop, Web APIs, (Micro)task Queue")
