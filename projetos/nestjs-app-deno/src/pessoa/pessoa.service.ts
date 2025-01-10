import { Injectable } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Pessoa } from './pessoa.entity.ts';
import { InsertResult, Repository } from 'typeorm';

@Injectable()
export class PessoaService {

    constructor(
        @InjectRepository(Pessoa)
        private pessoaRepository: Repository<Pessoa>,
    ) { }

    async create(pessoa: Pessoa): Promise<Number> {
        const insert = await this.pessoaRepository.insert(pessoa);
        return insert.identifiers[0].id;
    }
    
    findOne(id: number): Promise<Pessoa | null> {
        return this.pessoaRepository.findOneBy({ id });
    }
    
}
