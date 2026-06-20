package com.java.boticaintegrador.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class SmsService {

    // Leer credenciales desde application.properties
    @Value("${twilio.account-sid}")
    private String ACCOUNT_SID;

    @Value("${twilio.auth-token}")
    private String AUTH_TOKEN;

    @Value("${twilio.phone-number}")
    private String TWILIO_PHONE_NUMBER;

    private final Map<String, String> codigosVerificacion = new HashMap<>();

    // Inicializar Twilio con las credenciales
    private void initTwilio() {
        if (ACCOUNT_SID != null && AUTH_TOKEN != null) {
            Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        }
    }

    public String generarCodigo() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }

    public boolean enviarSms(String telefonoDestino, String codigo) {
        initTwilio();
        try {
            // Verificar si el número destino es el mismo que el de Twilio
            if (telefonoDestino.equals(TWILIO_PHONE_NUMBER)) {
                System.out.println("⚠️ No se puede enviar SMS al mismo número de Twilio");
                System.out.println("📱 Mostrando código en consola en su lugar");
                System.out.println("🔐 CÓDIGO: " + codigo);
                codigosVerificacion.put(telefonoDestino, codigo);
                return true;
            }

            // Enviar SMS real con Twilio
            Message message = Message.creator(
                new PhoneNumber(telefonoDestino),
                new PhoneNumber(TWILIO_PHONE_NUMBER),
                "Tu código de verificación Botica Control es: " + codigo
            ).create();
            
            System.out.println("✅ SMS ENVIADO EXITOSAMENTE");
            System.out.println("📱 De: " + TWILIO_PHONE_NUMBER);
            System.out.println("📱 A: " + telefonoDestino);
            System.out.println("🔐 Código: " + codigo);
            
            codigosVerificacion.put(telefonoDestino, codigo);
            return true;
            
        } catch (Exception e) {
            System.out.println("❌ Error al enviar SMS: " + e.getMessage());
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
            System.out.println("✅ Código verificado correctamente para: " + telefono);
        }
        return valido;
    }

    public String obtenerNumeroVerificacion() {
        return "+51941233970";
    }
}