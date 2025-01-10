package study.crudspring.pessoa;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PessoaService {
    private final PessoaRepository pessoaRepository;
    private final RedisTemplate<Long, PessoaDTO> redisTemplate;

    public PessoaService(PessoaRepository pessoaRepository, RedisTemplate<Long, PessoaDTO> redisTemplate) {
        this.pessoaRepository = pessoaRepository;
        this.redisTemplate = redisTemplate;
    }

    @Transactional
    public Long createPessoa(PessoaDTO pessoaDTO) {
        PessoaEntity pessoaEntity = new PessoaEntity();
        pessoaEntity.setNome(pessoaDTO.getNome());
        pessoaEntity.setApelido(pessoaDTO.getApelido());
        pessoaEntity.setNascimento(pessoaDTO.getNascimento());

        pessoaRepository.save(pessoaEntity);
        return pessoaEntity.getId();
    }

    public Optional<PessoaDTO> findById(Long id) {
        Optional<PessoaEntity> findById = pessoaRepository.findById(id);

        return findById.map(entity -> {
            PessoaDTO dto = new PessoaDTO();
            dto.setId(entity.getId());
            dto.setNome(entity.getNome());
            dto.setApelido(entity.getApelido());
            dto.setNascimento(entity.getNascimento());
            return dto;
        });
    }

    public Optional<PessoaDTO> findByIdUsingCache(Long id) {
        PessoaDTO pessoaDTO = redisTemplate.opsForValue().get(id);
        if(pessoaDTO != null) {
            return Optional.of(pessoaDTO);
        }
        Optional<PessoaDTO> fromDb = findById(id);
        fromDb.ifPresent(dto -> redisTemplate.opsForValue().set(id, dto));
        return fromDb;
    }

}
