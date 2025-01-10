import {check} from 'k6'
import http from 'k6/http'
import exec from 'k6/execution'

const url = 'http://localhost:8080/pessoas'

function escrita(id) {

    const payload = JSON.stringify({
        apelido: `coder ${id}`,
        nome: `Coder Name XXX ${id}`,
        nascimento: '2020-10-10',
    })

    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    }

    const resp = http.post(url, payload, params)
    check(resp, {
        'POST should return 201': (r) => r.status === 201
    })
}

export function leitura (id) {
    const resp = http.get(`${url}/${id}`)
    check(resp, {
        'GET should return 200': (r) => r.status === 200
    })
}

export function leituraCache (id) {
    const resp = http.get(`${url}/${id}/cache`)
    check(resp, {
        'GET from cache should return 200': (r) => r.status === 200
    })
}


export default function () {
    const id = exec.vu.iterationInScenario + 1
    console.log({id})
    escrita(id)
    leitura(id)
    leituraCache(id)
}