package br.com.compass.domain;


import br.com.compass.dao.TipoTransacao;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transacoes")
public class Transacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conta_id", nullable = false)
    private Conta conta;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoTransacao tipo;

    @Column(nullable = false)
    private BigDecimal valor;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    public Transacao() {
        this.dataHora = LocalDateTime.now();
    }

    public Transacao(TipoTransacao tipo, BigDecimal valor, Conta conta) {
        this();
        this.tipo = tipo;
        this.valor = valor;
        this.conta = conta;
        this.dataHora = LocalDateTime.now();;
    }

    public TipoTransacao getTipo() {
        return tipo;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    @Override
    public String toString() {
        return "Transacao{" +
                "id=" + id +
                ", conta=" + conta +
                ", tipo='" + tipo + '\'' +
                ", valor=" + valor +
                ", dataHora=" + dataHora +
                '}';
    }
}
