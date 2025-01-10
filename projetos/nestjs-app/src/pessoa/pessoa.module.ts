import { Module } from '@nestjs/common';
import { PessoaController } from './pessoa.controller';
import { Pessoa } from './pessoa.entity';
import { TypeOrmModule } from '@nestjs/typeorm';
import { PessoaService } from './pessoa.service';

@Module({
  imports: [TypeOrmModule.forFeature([Pessoa])],
  controllers: [PessoaController],
  providers: [PessoaService]
})
export class PessoaModule {}
