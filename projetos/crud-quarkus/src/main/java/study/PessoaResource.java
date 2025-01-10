package study;

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
    public Response create(PessoaDTO pessoa) {
        var id = pessoaService.createPessoa(pessoa);
        return Response.created(URI.create("/pessoas/" + id)).build();
    }

    @GET
    @Path("{id}")
    public Response findById(@PathParam("id") Long id) {
        var resp = pessoaService.findById(id);
        if (resp == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(resp).build();
    }


    @GET
    @Path("{id}/cache")
    public Response findByIdFromCache(@PathParam("id") Long id) {
        var resp = pessoaService.findByIdFromCache(id);
        if (resp == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(resp).build();
    }

}