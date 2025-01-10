package study.crudspring.pessoa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/pessoas")
public class PessoaController {
    private final PessoaService pessoaService;

    public PessoaController(PessoaService pessoaService) {
        this.pessoaService = pessoaService;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody PessoaDTO pessoa) {
        Long id = pessoaService.createPessoa(pessoa);
        return ResponseEntity.created(URI.create("/pessoas/" + id)).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PessoaDTO> findById(@PathVariable("id") Long id) {
        return pessoaService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/cache")
    public ResponseEntity<PessoaDTO> findByIdUsingCache(@PathVariable("id") Long id) {
        return pessoaService.findByIdUsingCache(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
