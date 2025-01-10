import {check} from 'k6'
import http from 'k6/http'
import exec from 'k6/execution'

const url = 'http://localhost:8080/pessoas'

export default function () {
    const n = exec.vu.iterationInScenario
    const id = (n % 1_000) + 1
    const resp = http.get(`${url}/${id}`)
    check(resp, {
        'should return 200': (r) => r.status === 200
    })
}

export function handleSummary(data) {
    return {
        [__ENV.OUTPUT_FILE]: JSON.stringify(data, null, 4),
    }
}
