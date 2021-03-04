# Пробный вариант парсинга JOSE в web-приложении

Проект демонстрирует возможный вариант парсинга JOSE (JWS или JWE) с помощью кастомного HttpMessageConverter. 
Это позволит разместить логику по парсингу, проверке/формированию подписи, шифрованию/дешифрованию в едином месте, 
скрыть от прикладных разработчиков детали реализации и облегчить им жизнь, не вынуждая их каждый раз копипастить

## Постановка задачи со стороны бизнеса:

По умолчанию контроллеры для документов должны уметь работать с запросами в формате JSON или JWS. 
В настройках должен быть флаг "Необходимо шифрование", включающий обязательное шифрование запросов и ответов. 
При этом этот флаг должен действовать для одних документов, а на другие не распространяться.

Если флаг выключен, то:
* Разрешено принимать запросы и отвечать в формате JSON или JWS. 
  Зависит от того, эндпоинты с какими consumes и produces будут реализованы в конретно взятом контроллере, 
  и от того, что пришлет пользователь в заголовках Content-Type и Accept
* Запрещено использование формата JWE

Если флаг выключен, то:
* Запрещено принимать запросы и отвечать в формате JSON или JWS
* Обязательно использование формата JWE как в запросе, так и в ответе, 
  при этом пользователь обязан указать в запросе как заголовок Content-Type: application/jose, так и Accept: application/jose

## Решение:

В спринге для сериализации/десериализации Java-объектов в/из JSON/XML и т.д. при работе с HTTP-запросами используется механизм HttpMessageConverter'ов.
В спринге существует несколько дефолтных MessageConverter'ов. 
Например для сериализации/десериализации JSON обычно по умолчанию спрингом регистрируется MappingJackson2HttpMessageConverter.
Поэтому не будем изобретать костылей и решаим задачу согласно правилам спринга. 
Воспользуемся этим механизмом: создадим кастомный HttpMessageConverter и зарегистрируем его для обработки формата application/jose.

Базовая проверка, что при включенном обязательном шифровании пользователь прислал как заголовок Content-Type: application/jose, так и Accept: application/jose
находится в CustomHandlerInterceptor.
Основная логика по сериализации/десериализации находится в JoseHttpMessageConverter. Также там делаются дополнительные проверки на случай,
если пользователь при включенном шифровании вместе с заголовком application/jose прислал в теле JWS вместо JWE. 
Или наоборот при выключенном шифровании вместе с заголовком application/jose прислал в теле JWE вместо JWS.

В проекте есть два контроллера для двух разных документов. 
* JweAllowedDocumentController - контроллер для документа, на который распространяется флаг шифрования, т.е. возможна работа с документом, используя все форматы: JSON, JWS, JWE
* SimpleDocumentController - контроллер для документа, на который НЕ распространяется флаг шифрования, т.е. возможна работа с документом, используя только форматы JSON или JWS

Вызвать контроллеры можно, запуская тестовые HTTP-запросы из файлов test-jwe-allowed-document.http и test-simple-document.http

Флаг обязательности шифрования настраивается в application.properties в свойстве needTechEncryption.

Если флаг needTechEncryption=true, то для всех контроллеров, маркированных аннотацией @JweAllowed, будет разрешен 
для приёма и возврата только формат JWE, остальные форматы запрещены.
На контроллеры, не отмеченые аннотацией @JweAllowed, действие флага не распространяется, при этом формат JWE запрещен.

Если флаг needTechEncryption=false, то для всех контроллеров запрещен формат JWE, разрешенность остальных форматов определяется, 
исходя из маппингов эндпоинтов в каждом контретном контроллере.

## Особенности реализации:
* Кастомный JoseHttpMessageConverter наследуется от MappingJackson2HttpMessageConverter, 
т.к. по сути JoseHttpMessageConverter всего лишь распаковывает или формирует JWS или JWE контейнер над JSON-payload,
а дальнейшая сериализация/десериализация в/из JSON передается в родительский MappingJackson2HttpMessageConverter.
Таким образом обработка самого JSON идёт один в один таким же образом, как и при обычных HTTP-запросах с application/json форматом.
Но это не обязательно и можно например отнаследовать JoseHttpMessageConverter от AbstractHttpMessageConverter и самим реализовать
сериализацию/десериализацию в/из JSON, используя ObjectMapper.

## Преимущества:
* Вся логика по сериализации/десериализации в/из формат application/jose вынесена в кастомный JoseHttpMessageConverter. 
Проверки присланных пользователем заголовков, тела и флага шифрования вынесены в единое место, и эти проверки не нужно каждый раз копипастить.
* От сервисного слоя скрыта инф-ия о том, в каком формате приходит объект документа или в каком формате его нужно вернуть. 
Прикладному разработчику не нужно постоянно копипастить портянку из методов create, createFromJws, createFromJwe и т.д.
* Из слоя с контроллерами вынесена логика сериализация/десериализация в/из формат application/jose.
В контроллере необходимо лишь настроить необходимые для данного конкретного документа эндпоинты, используя маппинги с consumes и produces, 
и при необходимости указать аннотацию @JweAllowed.   

## Недостатки:
* Т.к. JoseHttpMessageConverter отнаследован от MappingJackson2HttpMessageConverter, то в кач-ве payload может исп-ся
только JSON. Другие форматы, которые не сможет обработать MappingJackson2HttpMessageConverter, например XML, не поддерживаются.
Хотя тут возможно есть решение, нужно думать, т.к. JoseHttpMessageConverter не обязательно наследваоть от MappingJackson2HttpMessageConverter, 
см. пункт "Особенности реализации"
* По-хорошему JoseHttpMessageConverter должен быть максимально простым и тупо сериализовать/десериализовать в/из JOSE (JWS или JWE) без каких-либо проверок,
без знания о флаге шифрования, без знания о том, что флаг распространяется не на все документы.
Но это не так, т.к. бизнес-постановка нетривиальна. Плюс накладывается тот факт, что RFC определяет всего лишь один MediaType application/jose,
 и под ним может скрываться как JWS, так и JWE 