package study.crud_vertx;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.pgclient.PgBuilder;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisAPI;
import io.vertx.redis.client.RedisOptions;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Tuple;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MainVerticle extends VerticleBase {

  static String getEnv(String name, String defaultValue) {
    return System.getenv().getOrDefault(name, defaultValue);
  }

  @Override
  public Future<?> start() {

    PgConnectOptions connectOptions = new PgConnectOptions()
      .setPort(Integer.parseInt(getEnv("DATASOURCE_PORT", "5432")))
      .setHost(getEnv("DATASOURCE_HOST", "localhost"))
      .setDatabase(getEnv("DATASOURCE_DATABASE", "postgres"))
      .setUser(getEnv("DATASOURCE_USERNAME", "postgres"))
      .setPassword(getEnv("DATASOURCE_PASSWORD", "postgres"));

    PoolOptions poolOptions = new PoolOptions()
//TODO:      .setEventLoopSize(2)  ?? need this ???
      .setMaxSize(16);

    RedisOptions options = new RedisOptions();
    options.setConnectionString(getEnv("REDIS_CONNECTION_STRING", "redis://localhost:6379"));
    Redis redisClient = Redis.createClient(vertx, options);
    RedisAPI redis = RedisAPI.api(redisClient);

    Pool pool = PgBuilder
      .pool()
      .with(poolOptions)
      .connectingTo(connectOptions)
      .using(vertx)
      .build();


    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());

    router.route("/ping").handler(ctx -> {
      HttpServerResponse response = ctx.response();
      response.end();
    });

    router.post("/pessoas")
      //TODO add transaction ???
      .handler(ctx -> {

        JsonObject json = ctx.body().asJsonObject();

        var nascimento = LocalDate.parse(json.getString("nascimento"));
        pool.getConnection().flatMap(conn ->
          conn.preparedQuery("INSERT INTO pessoas (nome, apelido, nascimento) VALUES ($1, $2, $3) RETURNING id")
            .execute(Tuple.of(json.getString("nome"), json.getString("apelido"), nascimento))
            .map(rows -> {
              var it = rows.iterator();
              if (it.hasNext()) {
                var row = it.next();
                return row.getLong("id");
              }
              return null;
            }).onComplete(ar -> conn.close())
        ).onComplete(resp -> {
          if (resp.failed()) {
            ctx.fail(resp.cause());
            return;
          }
          HttpServerResponse response = ctx.response();
          response.setStatusCode(HttpResponseStatus.CREATED.code());
          response.putHeader("Location", "/pessoas/" + resp.result());
          response.end();
        });
      });

    router.get("/pessoas/:id").handler(ctx -> {

      long id;

      try {
        id = Long.parseLong(ctx.pathParam("id"));
      } catch (NumberFormatException e) {
        ctx.response().setStatusCode(HttpResponseStatus.BAD_REQUEST.code()).end();
        return;
      }

      pool.getConnection()
        .flatMap(conn ->
          conn.preparedQuery("SELECT * FROM pessoas WHERE id = $1")
            .execute(Tuple.of(id))
            .map(rowSet -> {
              var it = rowSet.iterator();
              if (it.hasNext()) {
                var row = it.next();
                return new JsonObject()
                  .put("id", row.getLong("id"))
                  .put("nome", row.getString("nome"))
                  .put("apelido", row.getString("apelido"))
                  .put("nascimento", row.getLocalDate("nascimento").format(DateTimeFormatter.ISO_LOCAL_DATE));
              }
              return null;
            })
            .onComplete((i) -> conn.close())
        ).onComplete(result -> {
          if (result.failed()) {
            ctx.fail(result.cause());
          }
          var json = result.result();
          if (json != null) {
            ctx.json(json);
          } else {
            ctx.response().setStatusCode(HttpResponseStatus.NOT_FOUND.code()).end();
          }
        });

    });


    router.get("/pessoas/:id/cache").handler(ctx -> {
      String id = ctx.pathParam("id");
      redis.get(id)
        .onFailure(ctx::fail)
        .onSuccess(response -> {
          if (response == null) {


            pool.getConnection()
              .flatMap(conn ->
                conn.preparedQuery("SELECT * FROM pessoas WHERE id = $1")
                  .execute(Tuple.of(Long.parseLong(id)))
                  .map(rowSet -> {
                    var it = rowSet.iterator();
                    if (it.hasNext()) {
                      var row = it.next();
                      return new JsonObject()
                        .put("id", row.getLong("id"))
                        .put("nome", row.getString("nome"))
                        .put("apelido", row.getString("apelido"))
                        .put("nascimento", row.getLocalDate("nascimento").format(DateTimeFormatter.ISO_LOCAL_DATE));
                    }
                    return null;
                  })
                  .onComplete((i) -> conn.close())
              ).onComplete(result -> {
                if (result.failed()) {
                  ctx.fail(result.cause());
                }
                var json = result.result();
                if (json != null) {
                  redis.append(id, json.encode()).onSuccess((r) -> {});
                  ctx.json(json);
                } else {
                  ctx.response().setStatusCode(HttpResponseStatus.NOT_FOUND.code()).end();
                }
              });

            return;
          }
          ctx.response().putHeader("Content-Type","application/json").end(response.toBuffer());
        });


    });

    int port = Integer.parseInt(getEnv("PORT", "8080"));
    return vertx.createHttpServer()
      .requestHandler(router)
      .listen(port)
      .onSuccess(http -> System.out.println("HTTP server started on port: " + port));
  }
}
