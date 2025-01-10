import {check} from 'k6'
import http from 'k6/http'
import exec from 'k6/execution'


const url = 'http://localhost:8080/pessoas'

export default function () {
    const n = exec.vu.iterationInScenario

    const payload = JSON.stringify({
        apelido: `coder ${n}`,
        nome: `Coder Name XXX ${n}`,
        nascimento: '2020-10-10',
    })

    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    }

    const resp = http.post(url, payload, params)
//   console.log(resp)
    check(resp, {
        'should return 201': (r) => r.status === 201
    })
}

export function handleSummary(data) {
    return {
        [__ENV.OUTPUT_FILE]: JSON.stringify(data, null, 4),
    }
}