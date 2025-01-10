package study.crudspring;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import study.crudspring.pessoa.PessoaDTO;

@SpringBootApplication
public class CrudSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrudSpringApplication.class, args);
    }

    @Bean
    public RedisTemplate<Long, PessoaDTO> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<Long, PessoaDTO> template = new RedisTemplate<>();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        Jackson2JsonRedisSerializer<PessoaDTO> serializer = new Jackson2JsonRedisSerializer<>(objectMapper, PessoaDTO.class);
        template.setDefaultSerializer(serializer);
        GenericToStringSerializer<Long> longToStringSerializer = new GenericToStringSerializer<>(Long.class);
        template.setKeySerializer(longToStringSerializer);
        template.setConnectionFactory(connectionFactory);

        return template;
    }

}
