package com.java.boticaintegrador.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class SmsService {

    private static final String ACCOUNT_SID = "AC6168196d54ef765f6864ac1b68e9586c";
    private static final String AUTH_TOKEN = "81a6b036a76ca432d1cec8ed9e1fd6f1";
    private static final String TWILIO_PHONE_NUMBER = "+18172420459";

    private final Map<String, String> codigosVerificacion = new HashMap<>();

    static {
        try {
            Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
            System.out.println("✅ Twilio inicializado correctamente");
        } catch (Exception e) {
            System.out.println("❌ Error inicializando Twilio: " + e.getMessage());
        }
    }

    public String generarCodigo() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }

    public boolean enviarSms(String telefonoDestino, String codigo) {
        try {
            System.out.println("📱 Intentando enviar SMS...");
            System.out.println("📱 De: " + TWILIO_PHONE_NUMBER);
            System.out.println("📱 A: " + telefonoDestino);
            System.out.println("🔐 Código: " + codigo);

            // Verificar si el número destino es el mismo que el de Twilio
            if (telefonoDestino.equals(TWILIO_PHONE_NUMBER)) {
                System.out.println("⚠️ No se puede enviar al mismo número");
                codigosVerificacion.put(telefonoDestino, codigo);
                return true;
            }

            // Enviar SMS real con Twilio
            Message message = Message.creator(
                new PhoneNumber(telefonoDestino),
                new PhoneNumber(TWILIO_PHONE_NUMBER),
                "Tu código de verificación Botica Control es: " + codigo
            ).create();
            
            System.out.println("✅ SMS ENVIADO!");
            System.out.println("📨 SID: " + message.getSid());
            System.out.println("📊 Estado: " + message.getStatus());
            
            codigosVerificacion.put(telefonoDestino, codigo);
            return true;
            
        } catch (Exception e) {
            System.out.println("❌ ERROR DETALLADO:");
            e.printStackTrace();
            // Fallback: mostrar en consola
            System.out.println("📱 Código (fallback): " + codigo);
            codigosVerificacion.put(telefonoDestino, codigo);
            return true;
        }
    }

    public boolean verificarCodigo(String telefono, String codigoIngresado) {
        String codigoGuardado = codigosVerificacion.get(telefono);
        if (codigoGuardado == null) {
            System.out.println("❌ No hay código guardado para: " + telefono);
            return false;
        }
        
        boolean valido = codigoGuardado.equals(codigoIngresado);
        if (valido) {
            codigosVerificacion.remove(telefono);
            System.out.println("✅ Código verificado correctamente");
        } else {
            System.out.println("❌ Código incorrecto");
        }
        return valido;
    }

    public String obtenerNumeroVerificacion() {
        return "+51941233970";
    }
}