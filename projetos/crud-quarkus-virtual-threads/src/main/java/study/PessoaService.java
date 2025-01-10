package study;

import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.value.ValueCommands;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class PessoaService {
    private final PessoaRepository pessoaRepository;
    private final ValueCommands<Long, PessoaDTO> redisValue;

    @Inject
    public PessoaService(PessoaRepository pessoaRepository, RedisDataSource redisDataSource) {
        this.pessoaRepository = pessoaRepository;
        this.redisValue = redisDataSource.value(Long.class, PessoaDTO.class);
    }

    @Transactional
    public Long createPessoa(PessoaDTO pessoaDTO) {
        PessoaEntity pessoaEntity = new PessoaEntity();
        pessoaEntity.setNome(pessoaDTO.getNome());
        pessoaEntity.setApelido(pessoaDTO.getApelido());
        pessoaEntity.setNascimento(pessoaDTO.getNascimento());

        pessoaRepository.persist(pessoaEntity);
        return pessoaEntity.getId();
    }

    public PessoaDTO findById(Long id) {
        PessoaEntity byId = pessoaRepository.findById(id);
        if (byId == null) {
            return null;
        }
        PessoaDTO dto = new PessoaDTO();
        dto.setId(byId.getId());
        dto.setNome(byId.getNome());
        dto.setApelido(byId.getApelido());
        dto.setNascimento(byId.getNascimento());
        return dto;
    }

    public PessoaDTO findByIdFromCache(Long id) {
        var fromCache = redisValue.get(id);
        if (fromCache != null) {
            return fromCache;
        }
        var fromDb = findById(id);
        if (fromDb != null) {
            redisValue.set(id, fromDb);
        }
        return fromDb;
    }
}
