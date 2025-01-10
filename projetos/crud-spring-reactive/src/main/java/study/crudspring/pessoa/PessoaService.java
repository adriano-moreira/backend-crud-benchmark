package study.crudspring.pessoa;

import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class PessoaService {
    private final PessoaRepository pessoaRepository;
    private final ReactiveRedisOperations<Long, PessoaDTO> redisOps;

    public PessoaService(PessoaRepository pessoaRepository,
                         ReactiveRedisOperations<Long, PessoaDTO> redisValueOps) {
        this.pessoaRepository = pessoaRepository;
        this.redisOps = redisValueOps;
    }

    public Mono<Long> createPessoa(PessoaDTO pessoaDTO) {
        PessoaEntity pessoaEntity = new PessoaEntity();
        pessoaEntity.setNome(pessoaDTO.getNome());
        pessoaEntity.setApelido(pessoaDTO.getApelido());
        pessoaEntity.setNascimento(pessoaDTO.getNascimento());

        return pessoaRepository.save(pessoaEntity)
                .map(PessoaEntity::getId);

    }

    public Mono<PessoaDTO> findById(Long id) {
        return pessoaRepository.findById(id).mapNotNull(entity -> {
            PessoaDTO dto = new PessoaDTO();
            dto.setId(entity.getId());
            dto.setNome(entity.getNome());
            dto.setApelido(entity.getApelido());
            dto.setNascimento(entity.getNascimento());
            return dto;
        });
    }

    public Mono<PessoaDTO> findByIdUsingCache(Long id) {
        return redisOps.opsForValue().get(id)
                .switchIfEmpty(
                        findById(id).flatMap(fromDb ->
                                redisOps.opsForValue().set(fromDb.getId(), fromDb).thenReturn(fromDb)
                        )
                );
    }
}
