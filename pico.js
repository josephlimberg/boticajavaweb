import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    stages: [
        { duration: '5s', target: 0 },    // Arranca en 0
        { duration: '10s', target: 50 },  // Sube a 50 usuarios en 10s (PICO)
        { duration: '30s', target: 50 },  // Mantén 50 usuarios por 30s
        { duration: '10s', target: 0 },   // Baja a 0
    ],
    thresholds: {
        http_req_duration: ['p(95)<5000'], // Toleramos 5s para pico
        http_req_failed: ['rate<0.10'],    // Toleramos 10% de errores
    },
};

export default function () {
    // 1. Login
    const loginRes = http.post('http://localhost:8080/login', {
        username: 'admin',
        password: 'admin123'
    }, { headers: { 'Content-Type': 'application/x-www-form-urlencoded' } });

    check(loginRes, { 'Login exitoso': (r) => r.status === 200 || r.status === 302 });
    if (loginRes.status !== 200 && loginRes.status !== 302) return;

    sleep(1);

    // 2. Listar medicamentos
    const medRes = http.get('http://localhost:8080/medicamentos');
    check(medRes, { 'Listado exitoso': (r) => r.status === 200 });

    sleep(1);

    // 3. Registrar venta
    const ventaPayload = {
        detalles: '[{"medicamentoId":1,"cantidad":1,"precioUnitario":15.50,"subtotal":15.50}]',
        total: '18.29',
        subtotal: '15.50',
        igv: '2.79',
        metodoPago: 'Efectivo',
        clienteNombre: 'Cliente Test'
    };

    const ventaRes = http.post('http://localhost:8080/ventas/registrar', ventaPayload, {
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
    });

    check(ventaRes, { 'Venta registrada': (r) => r.status === 200 || r.status === 302 });

    sleep(1.5);
}

export function handleSummary(data) {
    return {
        'reporte-pico.html': `
<!DOCTYPE html>
<html>
<head><title>Reporte Pico - Botica</title>
<style>
body { font-family: Arial; margin: 20px; background: #f5f5f5; }
.container { max-width: 800px; margin: auto; background: white; padding: 20px; border-radius: 10px; }
h1 { color: #2c3e50; }
.metric { display: inline-block; width: 45%; margin: 10px 2%; padding: 15px; background: #ecf0f1; border-radius: 8px; }
.value { font-size: 24px; font-weight: bold; }
.success { color: #27ae60; }
.danger { color: #e74c3c; }
</style>
</head>
<body>
<div class="container">
    <h1>📊 Prueba de PICO - Botica</h1>
    <p><strong>Fecha:</strong> ${new Date().toLocaleString()}</p>
    <p><strong>Escenario:</strong> Pico repentino de 50 usuarios en 10 segundos</p>
    <div class="metric">
        <div class="value">${data.metrics.iterations.values.count}</div>
        <div>Iteraciones completadas</div>
    </div>
    <div class="metric">
        <div class="value">${data.metrics.http_req_duration.values.avg.toFixed(2)} ms</div>
        <div>Tiempo promedio</div>
    </div>
    <div class="metric">
        <div class="value ${data.metrics.http_req_duration.values['p(95)'] < 5000 ? 'success' : 'danger'}">${data.metrics.http_req_duration.values['p(95)'].toFixed(2)} ms</div>
        <div>Percentil 95 (SLO < 5s) ${data.metrics.http_req_duration.values['p(95)'] < 5000 ? '✅' : '❌'}</div>
    </div>
    <div class="metric">
        <div class="value ${data.metrics.http_req_failed.values.rate < 0.10 ? 'success' : 'danger'}">${(data.metrics.http_req_failed.values.rate * 100).toFixed(2)}%</div>
        <div>Tasa de error (SLO < 10%) ${data.metrics.http_req_failed.values.rate < 0.10 ? '✅' : '❌'}</div>
    </div>
    <div class="metric">
        <div class="value">${data.metrics.http_reqs.values.count}</div>
        <div>Total peticiones</div>
    </div>
    <h2>✅ Verificación de SLOs</h2>
    <p>${data.metrics.http_req_duration.values['p(95)'] < 5000 && data.metrics.http_req_failed.values.rate < 0.10
            ? '✅ El sistema resiste el pico de 50 usuarios'
            : '⚠️ El sistema tuvo problemas con el pico'}</p>
    <p><em>Reporte generado con k6 - ${new Date().toLocaleString()}</em></p>
</div>
</body>
</html>`
    };
}