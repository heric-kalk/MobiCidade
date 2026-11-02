# Sistema MobiCidade

## Descrição

Sistema para gestão de uma frota compartilhada de bicicletas e patinetes elétricos em ambiente urbano, controlando estações de docking, usuários e o ciclo de vida de cada corrida — do desbloqueio na estação de origem até a devolução na estação de destino. O sistema deve controlar a autonomia de bateria dos patinetes e a necessidade de manutenção.

Como diferencial, o sistema consulta em tempo real as condições climáticas da cidade via API pública, bloqueando o início de novas corridas quando há previsão de chuva forte ou tempestade, por questão de segurança do usuário.

## Escopo desta implementação inicial

Esta etapa entrega as classes de domínio e seus relacionamentos, CRUDs em memória para Estação, Veículo, Usuário, Corrida e Manutenção, as regras de negócio (REQ01–REQ22) e a separação em camadas com interfaces. **As telas gráficas não foram implementadas**: o arquivo `modelo_navegacional_mobicidade.png` documenta as telas previstas e as transições entre elas. Ao executar o `Main` o sistema carrega dados de exemplo e exibe um painel de texto; há também uma simulação automática dos requisitos (veja "Como executar"). A interface com o usuário será feita em GUI.

Os dados ficam em memória e são perdidos ao encerrar o processo. Persistência em banco e interface gráfica ainda não fazem parte desta implementação inicial.

## Arquitetura

```
Main / ui (Painel, DadosIniciais, Simulacao)
        │  só conhece a fachada
        ▼
facade   IMobiCidadeFachada ◄── MobiCidadeFachada (raiz de composição)
        ▼
service  IServico<T,ID> ◄── ServicoBase ◄── EstacaoServico, VeiculoServico, UsuarioServico,
         CorridaServico, ManutencaoServico  (+ ClimaServico, RelatorioServico)
         ← camada única: casos de uso + regras de negócio (antigos Controller + Service)
        ▼
repository  IRepositorio<T,ID> ◄── RepositorioMemoria ◄── *RepositorioMemoria
        ▼
model    Estacao, Veiculo (Bicicleta/Patinete), Usuario (Comum/Assinante), Corrida,
         Manutencao, PrevisaoClimatica
```

- Interfaces começam com `I` (`IRepositorio`, `IEstacaoServico`, `IMobiCidadeFachada`, ...).
- O **model concentra o comportamento do domínio** (`Estacao.receberVeiculo/liberarVeiculo`, `Corrida.iniciar/finalizar/calcularTarifa`, `Manutencao.registrar/finalizar`, `Usuario.calcularCusto`, `PrevisaoClimatica.verificarAlerta`, `Patinete.consumirBateria`), e os **services decidem se a operação é permitida** (regras e restrições) antes de chamá-lo.
- Os relacionamentos são bidirecionais e mantidos em sincronia pelo model (Estação ↔ Veículo; Corrida → Usuário/Veículo/Estações; Manutenção → Veículo).

## Como executar

```
javac -encoding UTF-8 -d out $(find src/main/java -name '*.java')
java -cp out com.mobicidade.Main               # carrega dados de exemplo e exibe o painel
java -cp out com.mobicidade.Main --simulacao   # simulação automática REQ01–REQ22 (saída 0 = tudo OK)
```

## Mapeamento requisito → código

| Requisito | Onde está implementado |
|---|---|
| REQ01 | `EstacaoServico.validar`, `Estacao` |
| REQ02 | `VeiculoServico` (`prepararCadastro`, `validar`, `regrasDeCadastro`, `aposCadastrar`), `Veiculo`, `Bicicleta`, `Patinete` |
| REQ03 | `EstacaoServico.veiculosDisponiveis/vagasLivres` → `Estacao.veiculosDisponiveis/vagasLivres` |
| REQ04, REQ05 | `UsuarioServico.validar`, `Usuario`, `UsuarioComum`, `UsuarioAssinante` |
| REQ06 | `OpenMeteoProvedor` (HttpClient), `ClimaServico.consultar`, `PrevisaoClimatica` |
| REQ07 | `PrevisaoClimatica.verificarAlerta`, alerta no topo do `Painel` (tela inicial) |
| REQ08 | cache em `ClimaServico` + `PrevisaoClimatica.comoDesatualizada` |
| REQ09 | `CorridaServico.iniciar` → `Corrida.iniciar` |
| REQ10 | `CorridaServico.finalizar` → `Corrida.finalizar` |
| REQ11 | `Corrida.calcularTarifa`, `Usuario.calcularCusto`, `UsuarioAssinante` (consome minutos do plano) |
| REQ12 | `Corrida.TAXA_ZONA_LIVRE` (destino nulo = zona livre), `VeiculoServico.recolherParaEstacao` |
| REQ13 | `ManutencaoServico.registrar` → `Manutencao.registrar` |
| REQ14 | `ManutencaoServico.concluir` → `Manutencao.finalizar` |
| REQ15 | `RelatorioServico.corridasPorEstacao`, `corridasPorEstacaoCsv`, `exportarCorridasPorEstacaoCsv` |
| REQ16 | `RelatorioServico.faturamentoPorTipoUsuario` |
| REQ17 a REQ20 | `CorridaServico.regrasDeCadastro` (mensagens marcadas `[REQ17]` ... `[REQ20]`) |
| REQ21, REQ22 | `CorridaServico.finalizar` (mensagens marcadas `[REQ21]`, `[REQ22]`) |

Decisões de implementação: a duração da corrida é arredondada para cima em minutos (qualquer fração iniciada conta), então REQ22 só rejeita término igual ou anterior ao início; o consumo de bateria é de 0,5% por minuto de uso; sem nenhum dado climático (API fora do ar e sem cache) a corrida não é bloqueada, mas o sistema avisa que a previsão está indisponível; um usuário não pode ter duas corridas simultâneas.

## Requisitos Funcionais

### 1. Cadastro de Veículos e Estações
- REQ01: Cadastrar estações (identificação, localização — latitude/longitude, capacidade de vagas)
- REQ02: Cadastrar veículos vinculados a uma estação, com herança para Bicicleta e Patinete (Patinete possui nível de bateria em %)
- REQ03: Consultar disponibilidade de veículos e vagas livres em uma estação

### 2. Usuários
- REQ04: Cadastrar usuários (nome, CPF, forma de pagamento)
- REQ05: Implementar herança para tipos de usuário: Comum (paga por minuto) e Assinante (plano mensal com minutos inclusos)

### 3. Verificação Climática (API Pública)
- REQ06: Consultar a previsão do tempo atual e das próximas horas para a cidade usando a API pública **Open-Meteo** (sem chave)
- REQ07: Exibir alerta visual na tela inicial quando houver previsão de chuva forte ou tempestade nas próximas horas
- REQ08: Manter cache local da última consulta climática, sinalizando quando os dados exibidos não são atuais por indisponibilidade da API

### 4. Corridas
- REQ09: Iniciar corrida vinculando usuário, veículo e estação de origem, com data/hora de início
- REQ10: Finalizar corrida vinculando estação de destino e data/hora de término, calculando duração
- REQ11: Calcular tarifa da corrida (por minuto para usuário Comum, descontando minutos do plano para Assinante)
- REQ12: Aplicar taxa extra quando o veículo for devolvido fora de uma estação cadastrada ("zona livre")

### 5. Manutenção
- REQ13: Registrar ocorrência de manutenção em um veículo, retirando-o de circulação
- REQ14: Reintegrar veículo à frota após conclusão da manutenção, vinculando-o a uma estação

### 6. Relatórios
- REQ15: Relatório de corridas por estação de origem/destino em um período, exportável em CSV
- REQ16: Relatório de faturamento por tipo de usuário (Comum vs. Assinante)

### 7. Regras e Restrições
- REQ17: **Bloquear** o início de uma nova corrida quando a previsão climática indicar chuva forte ou tempestade na região
- REQ18: **Não permitir** o início de corrida com veículo em manutenção
- REQ19: **Impedir** o início de corrida com patinete cuja bateria esteja abaixo de um limiar mínimo configurável (ex.: 15%)
- REQ20: **Bloquear** o desbloqueio de um veículo em estação sem nenhum veículo disponível
- REQ21: **Validar** que a estação de destino tenha vaga disponível antes de confirmar a finalização da corrida
- REQ22: **Garantir** que toda corrida finalizada tenha duração maior que zero minutos

## Possíveis APIs/Bibliotecas

JavaFX, Open-Meteo API (https://open-meteo.com, pública e sem chave), `java.net.http.HttpClient`, Jackson/Gson, Java Time API, JUnit.

**Requisito bônus (opcional, fora da contagem oficial):** gerar um mapa/relatório HTML estático das estações com maior movimento do dia, aberto automaticamente no navegador via `Desktop.getDesktop().browse()`.

## Integrantes do grupo com nome completo
* Heric Costa do Nascimento - [heric.costa@ufrpe.br](mailto:heric.costa@ufrpe.br)
* Kelvis Kauan de Souza Gomes - [kelvis.kauan@ufrpe.br](mailto:kelvis.kauan@ufrpe.br)
* Eduarda Menezes Guilherme - [eduarda.guilherme@ufrpe.br](mailto:eduarda.guilherme@ufrpe.br)