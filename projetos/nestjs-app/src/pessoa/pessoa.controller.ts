import { Body, Controller, Get, Param, Post, Res } from '@nestjs/common';
import { Pessoa } from './pessoa.entity';
import { PessoaService } from './pessoa.service';
import { createClient, RedisClientType } from 'redis';


@Controller('pessoas')
export class PessoaController {
    redisClient: RedisClientType;

    constructor(private service: PessoaService) {
      this.redisClient = createClient({url: process.env.REDIS_CONNECTION_STRING});
      this.redisClient.connect();
    }

    shutdown() {
      this.redisClient.shutdown();
    }

    @Post()
    async create(@Body() pessoa: Pessoa, @Res() response: any) {
        const id = await this.service.create(pessoa);
        response.header('Location', `/pessoas/${id}`).end();
    }

    @Get(':id')
    findOne(@Param() params: any): Promise<Pessoa | null> {
      return this.service.findOne(params.id);
    }

    @Get(':id/cache')
    async findOneCached(@Param() params: any, @Res() response: any) {

      const id = params.id;

      const fromRedis = await this.redisClient.get(id)
      if(fromRedis != null) {
        response
          .header('Content-Type','application/json')
          .end(fromRedis);
        return;
      }

      const fromDB = await this.service.findOne(params.id);
      
      if (fromDB != null) {
        this.redisClient.set(id, JSON.stringify(fromDB))
      }

      if(fromDB == null) {
        response.status(404).end();
      } else {
        response.json(fromDB);
      }

    }
}
