package study;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PessoaRepository implements io.quarkus.hibernate.reactive.panache.PanacheRepository<PessoaEntity> {
}
