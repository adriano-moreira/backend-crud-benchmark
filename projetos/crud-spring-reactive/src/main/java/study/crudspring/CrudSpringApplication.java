package study.crudspring;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import study.crudspring.pessoa.PessoaDTO;

@SpringBootApplication
public class CrudSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrudSpringApplication.class, args);
    }

    @Bean
    ReactiveRedisOperations<Long, PessoaDTO> redisOperations(ReactiveRedisConnectionFactory factory) {
        GenericToStringSerializer<Long> longToStringSerializer = new GenericToStringSerializer<>(Long.class);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        Jackson2JsonRedisSerializer<PessoaDTO> serializer = new Jackson2JsonRedisSerializer<>(objectMapper, PessoaDTO.class);

        RedisSerializationContext.RedisSerializationContextBuilder<Long, PessoaDTO> builder =
                RedisSerializationContext.newSerializationContext(longToStringSerializer);

        RedisSerializationContext<Long, PessoaDTO> context = builder.value(serializer).build();
        return new ReactiveRedisTemplate<>(factory, context);
    }
}
