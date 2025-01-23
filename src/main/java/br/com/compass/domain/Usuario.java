package br.com.compass.domain;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String cpf;

    @Column(nullable = false)
    private String dtNascimento;

    @Column(nullable = false)
    private String numTelefone;

    public Usuario() {}

    public Usuario(String nome, String cpf, String dtNascimento, String numTelefone) {
        this.nome = nome;
        this.cpf = cpf;
        this.dtNascimento = dtNascimento;
        this.numTelefone = numTelefone;
    }

    public String getNome() {
        return nome;
    }

    public String getCpf() {
        return cpf;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", data de nascimento='" + dtNascimento + '\'' +
                ", número de telefone='" + numTelefone + '\'' +
                ", cpf='" + cpf + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(getCpf(), usuario.getCpf());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getCpf());
    }
}
