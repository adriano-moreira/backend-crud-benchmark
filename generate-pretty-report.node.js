/**
 * Responsavel por gerar um relatório agradável com base nos outputs do k6
 */

import * as fs from 'fs'

const benchmarkId = process.env.BENCHMARK_ID

const paddingStart = 12

const vus = [1, 20, 60]
const testes = ['teste-escrita', 'teste-leitura', 'teste-leitura-cache']
const ambientes = fs.readdirSync('ambientes', {withFileTypes: true})
    .filter(e => e.isDirectory())
    .map(e => e.name)
console.log('ambientes', ambientes)
const reportFile = `./relatorios/${benchmarkId}/report.md`


clearOrCreateReport()
writeln(`# Relatorio Benchmark ${benchmarkId}`)
// Throughput - Maior valor é melhor


writeln('|ambiente|teste|vu|p90|p99|max|throughput|fails|')
writeln('|--------|:---:|-:|--:|--:|--:|---------:|----:|')
for (let vu of vus) {
    for (let teste of testes) {
        writeln(`||${teste}|${vu}|||||`)
        for (let ambiente of ambientes) {

            const summary = readK6Summary(benchmarkId, teste, vu, ambiente);

            write('|')
            write(ambiente)
            write('|')
            write('')
            write('|')
            write('')
            write('|')
            write(summary.p90F)
            write('|')
            write(summary.p99F)
            write('|')
            write(summary.maxF)
            write('|')
            write(summary.throughputF)
            write('|')
            write(summary.failRateF)
            write('\n')
        }
    }
}
writeln('---')

function numberFormat(n) {
    // return new Intl.NumberFormat('pt-BR', {}).format(n)
    return n.toFixed(1).toString()
}

function formatTime(n) {
    if (n > 1) {
        return (n.toFixed(0).toString() + 'ms').padStart(paddingStart, ' ')
    }
    return (n * 1000.0).toFixed(0).toString() + 'µs'
}

function clearOrCreateReport() {
    fs.writeFileSync(reportFile, '');
}

function writeln(content) {
    fs.appendFileSync(reportFile, content + '\n')
}

function write(content) {
    fs.appendFileSync(reportFile, content)
}

/**
 * retorna principais propriedades formatadas, do realatorio do k6
 * @param {*} benchmarkId
 * @param {*} test
 * @param {*} vu
 * @param {*} environment
 */
function readK6Summary(benchmarkId, test, vu, environment) {
    const summary = readK6JSONSummary(benchmarkId, test, vu, environment)
    const failRate = (summary?.metrics.http_req_failed?.values?.rate ?? 0) * 100;
    const p99 = summary?.metrics.http_req_duration?.values['p(99)'] ?? 0
    const p90 = summary?.metrics.http_req_duration?.values['p(90)'] ?? 0
    const med = summary?.metrics.http_req_duration?.values.med ?? 0
    const max = summary?.metrics.http_req_duration?.values.max ?? 0
    const throughput = summary?.metrics.http_reqs?.values.rate ?? 0;


    const p99F = formatTime(p99)
    const p90F = formatTime(p90)
    const medF = formatTime(med)
    const maxF = formatTime(max)
    const throughputF = numberFormat(throughput) + ' req/s';
    const failRateF = numberFormat(failRate) + '%'

    return {
        failRate,
        p99,
        p90,
        med,
        max,
        throughput,

        p99F,
        p90F,
        medF,
        maxF,
        throughputF,
        failRateF,
    }

}

function readK6JSONSummary(benchmarkId, test, vu, environment) {
    const k6SummeryFile = `./relatorios/${benchmarkId}/k6/${test}-vu${vu}-${environment}.json`
    if (fs.existsSync(k6SummeryFile)) {
        try {
            return JSON.parse(fs.readFileSync(k6SummeryFile).toString());
        } catch (e) {
            console.error(`ERROR: reading ${k6SummeryFile}`, e)
        }
    } else {
        console.warn(`WARN: file not found: ${k6SummeryFile}`)
    }
}