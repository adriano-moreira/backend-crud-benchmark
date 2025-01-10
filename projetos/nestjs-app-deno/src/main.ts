import { NestFactory } from '@nestjs/core';
import { AppModule } from './app.module.ts';
import process from 'node:process';

async function bootstrap() {
  const app = await NestFactory.create(AppModule);
  await app.listen(process.env.PORT ?? 3000);
}
bootstrap();
