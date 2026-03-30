    package br.com.softhouse.dende.model;

    import jdk.dynalink.linker.LinkerServices;

    import java.time.LocalDateTime;
    import java.util.ArrayList;
    import java.util.List;
    import java.util.Objects;

    public class Evento {

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
        private int ingressosDisponiveis;
        private String localEvento;
        private boolean eventoAtivo = false;
        private final List<Ingresso> ingressos = new ArrayList<>();

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

        public enum Modalidade{
            PRESENCIAL,
            REMOTO,
            HIBRIDO
        }

        public Evento(){}


        public static EventoBuilder builder() {
            return new EventoBuilder();
        }


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
            private int ingressosDisponiveis;
            private String localEvento;
            private boolean eventoAtivo = false;


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

            public EventoBuilder ingressosDisponiveis(int ingressosDisponiveis) {
                this.ingressosDisponiveis = ingressosDisponiveis;
                return this;
            }

            public EventoBuilder localEvento(String localEvento) {
                this.localEvento = localEvento;
                return this;
            }

            public EventoBuilder eventoAtivo(boolean eventoAtivo) {
                this.eventoAtivo = eventoAtivo;
                return this;
            }



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
                evento.ingressosDisponiveis = this.ingressosDisponiveis;
                evento.localEvento = this.localEvento;
                evento.eventoAtivo = this.eventoAtivo;
                return evento;


            }


            }

        public Long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public void setOrganizador(Organizador organizador) { this.organizador = organizador; }
        public Organizador getOrganizador() {
            return organizador;
        }

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }

        public String getDescricao() {
            return descricao;
        }

        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }

        public String getPaginaWeb() {
            return paginaWeb;
        }

        public void setPaginaWeb(String paginaWeb) {
            this.paginaWeb = paginaWeb;
        }

        public LocalDateTime getDataInicio() {
            return dataInicio;
        }

        public void setDataInicio(LocalDateTime dataInicio) {
            this.dataInicio = dataInicio;
        }

        public LocalDateTime getDataFim() {
            return dataFim;
        }

        public void setDataFim(LocalDateTime dataFim) {
            this.dataFim = dataFim;
        }

        public TipoEvento getTipoEvento() {
            return tipoEvento;
        }

        public void setTipoEvento(TipoEvento tipoEvento) {
            this.tipoEvento = tipoEvento;
        }

        public Evento getEventoPrincipal() {
            return eventoPrincipal;
        }

        public void setEventoPrincipal(Evento eventoPrincipal) {
            this.eventoPrincipal = eventoPrincipal;
        }

        public Modalidade getModalidade() {
            return modalidade;
        }

        public void setModalidade(Modalidade modalidade) {
            this.modalidade = modalidade;
        }

        public double getPrecoUnitarioIngresso() {
            return precoUnitarioIngresso;
        }

        public void setPrecoUnitarioIngresso(double precoUnitarioIngresso) {
            this.precoUnitarioIngresso = precoUnitarioIngresso;
        }

        public double getTaxaCancelamento() {
            return taxaCancelamento;
        }

        public void setTaxaCancelamento(double taxaCancelamento) {
            this.taxaCancelamento = taxaCancelamento;
        }

        public boolean isEventoEstorno() {
            return eventoEstorno;
        }

        public void setEventoEstorno(boolean eventoEstorno) {
            this.eventoEstorno = eventoEstorno;
        }

        public int getCapacidadeMaxima() {
            return capacidadeMaxima;
        }

        public void setCapacidadeMaxima(int capacidadeMaxima) {
            this.capacidadeMaxima = capacidadeMaxima;
        }

        public int getIngressosDisponiveis() {
            return ingressosDisponiveis;
        }

        public void setIngressosDisponiveis(int ingressosDisponiveis) {
            this.ingressosDisponiveis = ingressosDisponiveis;
        }

        public String getLocalEvento() {
            return localEvento;
        }

        public void setLocalEvento(String localEvento) {
            this.localEvento = localEvento;
        }

        public boolean isEventoAtivo(){
            return eventoAtivo;
        }

        public void setEventoAtivo(boolean eventoAtivo) {
            this.eventoAtivo = eventoAtivo;
        }

        public void disponibilizarIngressos(int quantidade) {

            if (quantidade <= 0) {
                throw new IllegalArgumentException("Quantidade inválida.");
            }

            if (quantidade > capacidadeMaxima) {
                throw new IllegalArgumentException("Quantidade excede capacidade.");
            }

            this.ingressosDisponiveis = quantidade;
        }

        public void addIngresso(Ingresso ingresso){
            if (ingresso == null) return;

            if (!this.ingressos.contains(ingresso)){
                this.ingressos.add(ingresso);
            }
        }

        public void removeIngresso(Ingresso ingresso){
            if (ingresso == null) return;

            this.ingressos.remove(ingresso);
        }

        public List<Ingresso> getIngressos(){
            return List.copyOf(ingressos);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;

            Evento that = (Evento) obj;
            return id != null && id.equals(that.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }

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
