package study;

import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.redis.datasource.value.ReactiveValueCommands;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PessoaService {
    private final PessoaRepository pessoaRepository;
    private final ReactiveValueCommands<Long, PessoaDTO> redisValue;

    @Inject
    public PessoaService(PessoaRepository pessoaRepository, ReactiveRedisDataSource redisDataSource) {
        this.pessoaRepository = pessoaRepository;
        this.redisValue = redisDataSource.value(Long.class, PessoaDTO.class);
    }

    @WithTransaction
    public Uni<Long> createPessoa(PessoaDTO pessoaDTO) {
        PessoaEntity pessoaEntity = new PessoaEntity();
        pessoaEntity.setNome(pessoaDTO.getNome());
        pessoaEntity.setApelido(pessoaDTO.getApelido());
        pessoaEntity.setNascimento(pessoaDTO.getNascimento());

        return pessoaRepository.persist(pessoaEntity)
                //TODO: this return id null, should return persisted id
                .map(PessoaEntity::getId);
    }

    @WithSession
    public Uni<PessoaDTO> findById(Long id) {
        return pessoaRepository.findById(id).map(byId -> {
            if (byId == null) {
                return null;
            }
            PessoaDTO dto = new PessoaDTO();
            dto.setId(byId.getId());
            dto.setNome(byId.getNome());
            dto.setApelido(byId.getApelido());
            dto.setNascimento(byId.getNascimento());
            return dto;
        });
    }

    public Uni<PessoaDTO> findByIdFronCache(Long id) {
        return redisValue.get(id).flatMap(fromRedis -> {
            if (fromRedis != null) {
                return Uni.createFrom().item(fromRedis);
            }
            return findById(id).call(fromDb -> {
                if (fromDb != null) {
                    return redisValue.set(id, fromDb);
                }
                return Uni.createFrom().voidItem();
            });
        });
    }
}
