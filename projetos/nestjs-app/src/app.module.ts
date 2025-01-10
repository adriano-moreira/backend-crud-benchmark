import { Module } from '@nestjs/common';
import { AppService } from './app.service';
import { TypeOrmModule } from '@nestjs/typeorm';
import { PessoaModule } from './pessoa/pessoa.module';
import { Pessoa } from './pessoa/pessoa.entity';


@Module({
  imports: [
    TypeOrmModule.forRoot({
      type: 'postgres',
      host: process.env.DATASOURCE_HOST ?? 'localhost',
      port: parseInt(process.env.DATASOURCE_PORT ?? '5432'),
      username: process.env.DATASOURCE_USERNAME ?? 'root',
      password: process.env.DATASOURCE_PASSWORD ?? 'root',
      database: process.env.DATASOURCE_DATABASE ??  'test',
      poolSize: parseInt(process.env.DATASOURCE_POLL_SIZE ?? '16'),
      entities: [Pessoa],
      synchronize: false,
    }),
    PessoaModule,
  ],
  controllers: [],
  providers: [AppService],
})
export class AppModule {}
