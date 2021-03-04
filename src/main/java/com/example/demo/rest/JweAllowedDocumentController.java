package com.example.demo.rest;

import com.example.demo.rest.model.Document;
import com.example.demo.webconfig.JweAllowed;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Контроллер для документа, по которому разрешены запросы с любыми форматами, т.е. с всевозможными комбинациями
 * заголовков Content-Type и Accept
 */
@JweAllowed
@RestController
@RequestMapping(value = "/jwe-allowed-document")
public class JweAllowedDocumentController {

    // !!! Осторожно !!!
    // если пользователь не пришлет Content-Type, то выкинется ошибка 415, тут все ок,
    // но с produces работает по-другому: если пользователь не пришлет Accept, то ответ будет сформирован в формате,
    // идущем первым в списке produces (подробнее см. javadoc по produces)
    @PostMapping(value = "/create", consumes = {"application/json", "application/jose"}, produces = {"application/json", "application/jose"})
    public Document createDocument(@Valid  @RequestBody Document document) {
        return document;
    }
}
