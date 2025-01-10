import { Module } from '@nestjs/common';
import { PessoaController } from './pessoa.controller.ts';
import { Pessoa } from './pessoa.entity.ts';
import { TypeOrmModule } from '@nestjs/typeorm';
import { PessoaService } from './pessoa.service.ts';

@Module({
  imports: [TypeOrmModule.forFeature([Pessoa])],
  controllers: [PessoaController],
  providers: [PessoaService]
})
export class PessoaModule {}
