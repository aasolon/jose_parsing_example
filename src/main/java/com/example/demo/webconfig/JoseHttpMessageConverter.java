package com.example.demo.webconfig;

import com.example.demo.exception.BadRequestException;
import com.example.demo.service.CryptoService;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObject;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jose.JWEEncrypter;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

public class JoseHttpMessageConverter extends MappingJackson2HttpMessageConverter {

    private final JWSVerifier jwsVerifier;
    private final JWSSigner jwsSigner;
    private final JWEDecrypter jweDecrypter;
    private final JWEEncrypter jweEncrypter;
    private final CryptoService cryptoService;

    public JoseHttpMessageConverter(JWSVerifier jwsVerifier, JWSSigner jwsSigner, JWEDecrypter jweDecrypter,
                                    JWEEncrypter jweEncrypter, CryptoService cryptoService) {
        this.jwsVerifier = jwsVerifier;
        this.jwsSigner = jwsSigner;
        this.jweDecrypter = jweDecrypter;
        this.jweEncrypter = jweEncrypter;
        this.cryptoService = cryptoService;
    }

    @Override
    public Object read(Type type, Class<?> contextClass, HttpInputMessage inputMessage) throws HttpMessageNotReadableException {
        try {
            String joseContent = IOUtils.toString(inputMessage.getBody(), StandardCharsets.UTF_8);
            JOSEObject joseObject = JOSEObject.parse(joseContent);


            boolean onlyJweAllowed = CallContextHolder.getCallContext().isOnlyJweAllowed();
            if (joseObject instanceof JWEObject) {
                if (!onlyJweAllowed) {
                    throw new BadRequestException("Запрещено присылать запрос в формате JWE");
                }
                return readJwe((JWEObject) joseObject, type, contextClass, inputMessage);
            } else if (joseObject instanceof JWSObject) {
                if (onlyJweAllowed) {
                    throw new BadRequestException("Разрешено присылать запрос только в формате JWE");
                }
                return readJws((JWSObject) joseObject, type, contextClass, inputMessage);
            } else {
                throw new BadRequestException("Формат запроса не соответствует JWS или JWE");
            }


        } catch (BadRequestException e) {
            throw  e;
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при чтении JOSE", e);
        }
    }

    @Override
    protected void writeInternal(Object object, Type type, HttpOutputMessage outputMessage) throws HttpMessageNotWritableException {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            HttpOutputMessageWrapper httpOutputMessageWrapper = new HttpOutputMessageWrapper(outputMessage, byteArrayOutputStream);
            super.writeInternal(object, type, httpOutputMessageWrapper);
            String payload = byteArrayOutputStream.toString(StandardCharsets.UTF_8.name());

            String serializedJose = asJws(payload);
            boolean onlyJweAllowed = CallContextHolder.getCallContext().isOnlyJweAllowed();
            if (onlyJweAllowed) {
                serializedJose = asJwe(serializedJose);
            }

            try (Writer writer = new OutputStreamWriter(outputMessage.getBody(), StandardCharsets.UTF_8)) {
                IOUtils.write(serializedJose, writer);
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при записи JOSE", e);
        }
    }

    private Object readJwe(JWEObject jweObject, Type type, Class<?> contextClass, HttpInputMessage inputMessage) throws Exception {
        jweObject.decrypt(jweDecrypter);
        // в кач-ве дешифрованного payload из jwe должен быть jws
        JWSObject decryptedJWSObject = jweObject.getPayload().toJWSObject();
        return readJws(decryptedJWSObject, type, contextClass, inputMessage);
    }

    private Object readJws(JWSObject jwsObject, Type type, Class<?> contextClass, HttpInputMessage inputMessage) throws JOSEException, IOException {
        jwsObject.verify(jwsVerifier);

        // т.к. ранее уже считали InputStream из HttpInputMessage, то сформируем новый HttpInputMessage на основе оригинального
        // с той лишь разницой, что подложим в него новый InputStream, в котором будет содержаться уже не весь JWS, а лишь
        // json-payload из него
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(jwsObject.getPayload().toBytes());
        HttpInputMessage bufferedInputMessage = new HttpInputMessageWrapper(inputMessage, byteArrayInputStream);

        // вызываем родительский метод с новым HttpInputMessage, из которого он считает подложенный нами InputStream с JSON, т.е.
        // обработка JSON дальше пойдет обычным образом как если бы это был стандартный MappingJackson2HttpMessageConverter
        return super.read(type, contextClass, bufferedInputMessage);
    }

    private String asJws(String payload) throws JOSEException {
        JWSObject jwsObject = new JWSObject(
                new JWSHeader.Builder(new JWSAlgorithm("gost34.10-2012"))
                        .type(JOSEObjectType.JOSE)
                        .keyID(cryptoService.getTechnicalCertificateGuid())
                        .build(),
                new Payload(payload)
        );

        jwsObject.sign(jwsSigner);
        return jwsObject.serialize();
    }

    private String asJwe(String payload) throws JOSEException {
        JWEObject jweObject = new JWEObject(
                new JWEHeader.Builder(JWEAlgorithm.DIR, new EncryptionMethod("gost28147-89"))
                        .type(JOSEObjectType.JOSE)
                        .build(),
                new Payload(payload));
        jweObject.encrypt(jweEncrypter);
        return jweObject.serialize();
    }
}
