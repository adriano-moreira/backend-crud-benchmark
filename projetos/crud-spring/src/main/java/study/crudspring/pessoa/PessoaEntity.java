package study.crudspring.pessoa;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "pessoas")
public class PessoaEntity {

    @Id
    @Column(columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column()
    private String apelido;
    @Column()
    private String nome;
    @Column()
    @Temporal(TemporalType.DATE)
    private Date nascimento;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getApelido() {
        return apelido;
    }

    public void setApelido(String apelido) {
        this.apelido = apelido;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Date getNascimento() {
        return nascimento;
    }

    public void setNascimento(Date nascimento) {
        this.nascimento = nascimento;
    }
}
