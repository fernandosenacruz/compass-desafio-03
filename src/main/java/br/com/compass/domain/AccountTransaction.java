package br.com.compass.domain;


import br.com.compass.dao.TransactionType;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "account_transations")
public class AccountTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Column(nullable = false)
    private BigDecimal value;

    @Column(nullable = false, name = "date_time")
    private LocalDateTime dateTime;

    public AccountTransaction() {
        this.dateTime = LocalDateTime.now();
    }

    public AccountTransaction(TransactionType type, BigDecimal value, Account account) {
        this();
        this.type = type;
        this.value = value;
        this.account = account;
        this.dateTime = LocalDateTime.now();;
    }

    public TransactionType getType() {
        return type;
    }

    public BigDecimal getValue() {
        return value;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

}
