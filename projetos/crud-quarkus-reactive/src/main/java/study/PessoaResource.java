package study;

import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;

@Path("pessoas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PessoaResource {

    private final PessoaService pessoaService;

    @Inject
    public PessoaResource(PessoaService pessoaService) {
        this.pessoaService = pessoaService;
    }

    @POST
    public Uni<Response> create(PessoaDTO pessoa) {
        return pessoaService.createPessoa(pessoa)
                .map(id -> Response.created(URI.create("/pessoas/" + id)).build());
    }

    @GET
    @Path("{id}")
    public Uni<Response> findById(@PathParam("id") Long id) {
        return pessoaService.findById(id).map(resp -> {
            if (resp == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok(resp).build();
        });
    }

    @GET
    @Path("{id}/cache")
    public Uni<Response> findByIdFromCache(@PathParam("id") Long id) {
        return pessoaService.findByIdFronCache(id).map(resp -> {
            if (resp == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok(resp).build();
        });
    }

}
