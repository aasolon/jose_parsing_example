package com.example.demo.rest;

import com.example.demo.rest.model.Document;
import com.example.demo.service.DocumentService;
import com.example.demo.webconfig.JweRequired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * Контроллер для документа, по которому разрешены запросы с любыми форматами, т.е. с всевозможными комбинациями
 * заголовков Content-Type и Accept.
 * <p>
 * /{guid}/status - исключение, на него требование шифрования не распространяется
 */
@RestController
@RequestMapping(value = "/jwe-document")
public class JweDocumentController {

    private final DocumentService documentService;

    public JweDocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    // Аккуратно, если пользователь не пришлет Content-Type, то выкинется ошибка 415, тут все ок,
    // но с produces работает по-другому: если пользователь не пришлет Accept, то ответ будет сформирован в формате,
    // идущем ПЕРВЫМ в списке produces (подробнее см. javadoc по produces)
    //
    // Отличный пример как можно компактно, используя всего лишь один метод, покрыть все форматы.
    @JweRequired(jweConsumes = true, jweProduces = true)
    @PostMapping(value = "/create", consumes = {"application/json", "application/jose"}, produces = {"application/json", "application/jose"})
    public Document createDocument(@Valid  @RequestBody Document document) {
        return documentService.create(document);
    }

    @JweRequired(jweConsumes = true)
    @PostMapping(value = "/create-without-return", consumes = {"application/json", "application/jose"})
    public void createDocumentWithoutReturn(@Valid  @RequestBody Document document) {
        documentService.create(document);
    }

    @JweRequired(jweProduces = true)
    @GetMapping(value = "/{guid}", produces = {"application/json", "application/jose"})
    public Document getDocument(@PathVariable String guid) {
        return documentService.getDocument(guid);
    }

    @GetMapping(value = "/{guid}/status")
    public Map<String, String> getStatus(@PathVariable String guid) {
        Map<String, String> response = new HashMap<>();
        response.put("guid", guid);
        response.put("status", documentService.getStatus(guid));
        return response;
    }
}
