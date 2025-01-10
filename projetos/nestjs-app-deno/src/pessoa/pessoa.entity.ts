import { Entity, Column, PrimaryGeneratedColumn } from 'typeorm';

@Entity('pessoas')
export class Pessoa {
  @PrimaryGeneratedColumn()
  id?: number;

  @Column()
  nome?: string;

  @Column()
  apelido?: string;

  @Column({type: 'date'})
  nascimento?: Date;
}