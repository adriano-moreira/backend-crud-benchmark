package study.crudspring.pessoa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
@RequestMapping("/pessoas")
public class PessoaController {
    private final PessoaService pessoaService;

    public PessoaController(PessoaService pessoaService) {
        this.pessoaService = pessoaService;
    }

    @PostMapping
    public Mono<ResponseEntity<?>> create(@RequestBody PessoaDTO pessoa) {
        return pessoaService.createPessoa(pessoa)
                .map(id -> ResponseEntity.created(URI.create("/pessoas/" + id)).build());

    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<PessoaDTO>> findById(@PathVariable("id") Long id) {
        return pessoaService.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/cache")
    public Mono<ResponseEntity<PessoaDTO>> findByIdUsingCache(@PathVariable("id") Long id) {
        return pessoaService.findByIdUsingCache(id)
                .doOnError(Throwable::printStackTrace)
                .map(ResponseEntity::ok)
                .onErrorReturn(ResponseEntity.internalServerError().build())
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

}
