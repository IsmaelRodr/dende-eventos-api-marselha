    package br.com.softhouse.dende.model;

    import java.time.LocalDateTime;
    import java.util.ArrayList;
    import java.util.List;
    import java.util.Objects;

    public class Evento {

        //atributos
        private Long id;
        //atributo todo parte composição, onde o evento depende do organizador
        private Organizador organizador ;
        private String nome;
        private String descricao;
        private String paginaWeb;
        private LocalDateTime dataInicio;
        private LocalDateTime dataFim;
        private TipoEvento tipoEvento;
        //atributo que geralmente se encaixa como associação reflexiva (com ela mesmo)
        private Evento eventoPrincipal;
        //Um ENUM, pode ser considerado uma associação simples, mas após pesquisas
        //não se considera uma associação por serem classes especiais (os ENUMs).
        private Modalidade modalidade;
        private double precoUnitarioIngresso;
        private double taxaCancelamento;
        private boolean eventoEstorno;
        private int capacidadeMaxima;
        private int ingressosDisponiveis;
        private String localEvento;
        private boolean eventoAtivo = false;
        //atributo todo parte composição, onde as classes todo parte recebem a coleção das
        //classes que dependem dela.
        private final List<Ingresso> ingressos = new ArrayList<>();

        //classe privada do tipo ENUM
        public enum TipoEvento {
            SOCIAL,
            CORPORATIVO,
            ACADEMICO,
            CULTURAL,
            ENTRETENIMENTO,
            RELIGIOSOS,
            ESPORTIVOS,
            FEIRA,
            CONGRESSO,
            OFICINA,
            CURSO,
            TREINAMENTO,
            AULA,
            SEMINARIO,
            PALESTRA,
            SHOW,
            FESTIVAL,
            EXPOSICAO,
            RETIRO,
            CULTO,
            CELEBRACAO,
            CAMPEONATO,
            CORRIDA
        }

        //classe privada do tipo ENUM
        public enum Modalidade{
            PRESENCIAL,
            REMOTO,
            HIBRIDO
        }

        //Construtor padrão
        public Evento(){}

        //O builder (padrão de projetos) para flexibilizar a criação de instâncias
        //com múltiplos atributos.
        public static EventoBuilder builder() {
            // um factory method para retornar uma instância do builder para utilizar.
            return new EventoBuilder();
        }

        /*A classe do builder para a instância de objetos de forma flexível
        isto se dá pela montagem do objeto ao inserir os atributos sem um construtor
        rigido, com o objeto sendo construido no final*/
        public static class EventoBuilder{
            private Long id;
            private Organizador organizador ;
            private String nome;
            private String descricao;
            private String paginaWeb;
            private LocalDateTime dataInicio;
            private LocalDateTime dataFim;
            private TipoEvento tipoEvento;
            private Evento eventoPrincipal;
            private Modalidade modalidade;
            private double precoUnitarioIngresso;
            private double taxaCancelamento;
            private boolean eventoEstorno;
            private int capacidadeMaxima;
            private String localEvento;

            /*
            Esses atributos foram retirados, pois não fazem sentido serem atribuídos diretamente.

            private int ingressosDisponiveis;
            private boolean eventoAtivo = false;
            */

            public EventoBuilder id(Long id) {
                this.id = id;
                return this;
            }

            public EventoBuilder organizador(Organizador organizador) {
                this.organizador = organizador;
                return this;
            }

            public EventoBuilder nome(String nome) {
                this.nome = nome;
                return this;
            }

            public EventoBuilder descricao(String descricao) {
                this.descricao = descricao;
                return this;
            }

            public EventoBuilder paginaWeb(String paginaWeb) {
                this.paginaWeb = paginaWeb;
                return this;
            }

            public EventoBuilder dataInicio(LocalDateTime dataInicio) {
                this.dataInicio = dataInicio;
                return this;
            }

            public EventoBuilder dataFim(LocalDateTime dataFim) {
                this.dataFim = dataFim;
                return this;
            }

            public EventoBuilder tipoEvento(TipoEvento tipoEvento) {
                this.tipoEvento = tipoEvento;
                return this;
            }

            public EventoBuilder eventoPrincipal(Evento eventoPrincipal) {
                this.eventoPrincipal = eventoPrincipal;
                return this;
            }

            public EventoBuilder modalidade(Modalidade modalidade) {
                this.modalidade = modalidade;
                return this;
            }

            public EventoBuilder precoUnitarioIngresso(double precoUnitarioIngresso) {
                this.precoUnitarioIngresso = precoUnitarioIngresso;
                return this;
            }

            public EventoBuilder taxaCancelamento(double taxaCancelamento) {
                this.taxaCancelamento = taxaCancelamento;
                return this;
            }

            public EventoBuilder eventoEstorno(boolean eventoEstorno) {
                this.eventoEstorno = eventoEstorno;
                return this;
            }

            public EventoBuilder capacidadeMaxima(int capacidadeMaxima) {
                this.capacidadeMaxima = capacidadeMaxima;
                return this;
            }

            public EventoBuilder localEvento(String localEvento) {
                this.localEvento = localEvento;
                return this;
            }

            /*
            Os métodos foram retirados, pois não fazem sentido receberem atribuição direta.

            public EventoBuilder ingressosDisponiveis(int ingressosDisponiveis) {
                this.ingressosDisponiveis = ingressosDisponiveis;
                return this;
            }

            public EventoBuilder eventoAtivo(boolean eventoAtivo) {
                this.eventoAtivo = eventoAtivo;
                return this;
            }
            */

            public Evento build() {
                Evento evento = new Evento();

                evento.id = this.id;
                evento.organizador = this.organizador;
                evento.nome = this.nome;
                evento.descricao = this.descricao;
                evento.paginaWeb = this.paginaWeb;
                evento.dataInicio = this.dataInicio;
                evento.dataFim = this.dataFim;
                evento.tipoEvento = this.tipoEvento;
                evento.eventoPrincipal = this.eventoPrincipal;
                evento.modalidade = this.modalidade;
                evento.precoUnitarioIngresso = this.precoUnitarioIngresso;
                evento.taxaCancelamento = this.taxaCancelamento;
                evento.eventoEstorno = this.eventoEstorno;
                evento.capacidadeMaxima = this.capacidadeMaxima;
                evento.localEvento = this.localEvento;
                return evento;
                /*
                Não faz sentido esses atributos no builder.

                evento.ingressosDisponiveis = this.ingressosDisponiveis;
                evento.eventoAtivo = this.eventoAtivo;
                */
            }
        }

        //Setters
        public void setId(long id) {
            this.id = id;
        }
        public void setOrganizador(Organizador organizador) { this.organizador = organizador; }
        public void setNome(String nome) {
            this.nome = nome;
        }
        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }
        public void setPaginaWeb(String paginaWeb) {
            this.paginaWeb = paginaWeb;
        }
        public void setDataInicio(LocalDateTime dataInicio) {
            this.dataInicio = dataInicio;
        }
        public void setDataFim(LocalDateTime dataFim) {
            this.dataFim = dataFim;
        }
        public void setTipoEvento(TipoEvento tipoEvento) {
            this.tipoEvento = tipoEvento;
        }
        public void setEventoPrincipal(Evento eventoPrincipal) {
            this.eventoPrincipal = eventoPrincipal;
        }
        public void setModalidade(Modalidade modalidade) {
            this.modalidade = modalidade;
        }
        public void setPrecoUnitarioIngresso(double precoUnitarioIngresso) {
            this.precoUnitarioIngresso = precoUnitarioIngresso;
        }
        public void setTaxaCancelamento(double taxaCancelamento) {
            this.taxaCancelamento = taxaCancelamento;
        }
        public void setEventoEstorno(boolean eventoEstorno) {
            this.eventoEstorno = eventoEstorno;
        }
        public void setCapacidadeMaxima(int capacidadeMaxima) {
            this.capacidadeMaxima = capacidadeMaxima;
        }
        public void setLocalEvento(String localEvento) {
            this.localEvento = localEvento;
        }
        public void setEventoAtivo(boolean eventoAtivo) {
            this.eventoAtivo = eventoAtivo;
        }

        /*
        O método foi retirado, pois não faz sentido usar um setter quando há um método
        mais robusto definido.

        public void setIngressosDisponiveis(int ingressosDisponiveis) {
            this.ingressosDisponiveis = ingressosDisponiveis;
        }
        */

        //Getters
        public Long getId() {
            return id;
        }
        public String getNome() {
            return nome;
        }
        public String getDescricao() {
            return descricao;
        }
        public String getPaginaWeb() {
            return paginaWeb;
        }
        public LocalDateTime getDataInicio() {
            return dataInicio;
        }
        public LocalDateTime getDataFim() {
            return dataFim;
        }
        public TipoEvento getTipoEvento() {
            return tipoEvento;
        }
        public Evento getEventoPrincipal() {
            return eventoPrincipal;
        }
        public Modalidade getModalidade() {
            return modalidade;
        }
        public double getPrecoUnitarioIngresso() {
            return precoUnitarioIngresso;
        }
        public double getTaxaCancelamento() {
            return taxaCancelamento;
        }
        public int getIngressosDisponiveis() {
            return ingressosDisponiveis;
        }
        public String getLocalEvento() {
            return localEvento;
        }
        public int getCapacidadeMaxima() {
            return capacidadeMaxima;
        }

        //Booleans
        public boolean isEventoEstorno() {
            return eventoEstorno;
        }
        public boolean isEventoAtivo(){
            return eventoAtivo;
        }

        //Métodos para lidar com as coleções subordinadas.

        //Disponibilizar os ingressos quando um evento se torna ativo.
        public void disponibilizarIngressos(int quantidade) {

            if (quantidade <= 0) {
                throw new IllegalArgumentException("Quantidade inválida.");
            }

            if (quantidade > capacidadeMaxima) {
                throw new IllegalArgumentException("Quantidade excede capacidade.");
            }

            this.ingressosDisponiveis = quantidade;
        }

        //método para associar um evento e um ingresso.
        public void addIngresso(Ingresso ingresso){
            if (ingresso == null) return;

            if (!this.ingressos.contains(ingresso)){
                this.ingressos.add(ingresso);
            }
        }

        //método para desassociar um evento e um ingresso.
        public void removeIngresso(Ingresso ingresso){
            if (ingresso == null) return;

            this.ingressos.remove(ingresso);
        }

        //método para obter os ingressos associados ao evento.
        public List<Ingresso> getIngressos(){
            return List.copyOf(ingressos);
        }

        //Equals para comparar instâncias.
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;

            Evento that = (Evento) obj;
            return id != null && id.equals(that.id);
        }

        //Hashcode para representar unicamente a instância.
        @Override
        public int hashCode() {
            return Objects.hash(id);
        }

        //toString para representar textualmente a instância.
        @Override
        public String toString() {
            return "Evento{" +
                    "id=" + id +
                    ", nome='" + nome + '\'' +
                    ", dataInicio=" + dataInicio +
                    ", dataFim=" + dataFim +
                    ", tipoEvento=" + tipoEvento +
                    ", modalidade=" + modalidade +
                    ", preco=" + precoUnitarioIngresso +
                    ", ativo=" + eventoAtivo +
                    ", ingressosDisponiveis=" + ingressosDisponiveis +
                    '}';
        }
    }
