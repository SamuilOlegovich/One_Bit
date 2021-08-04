package com.samuilolegovich;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class SimpleGameApplication {
    public static void main(String[] args) {
        SpringApplication.run(SimpleGameApplication.class, args);
    }
    /*
    * в данном сервисе должна быть реализована вообще незамыславатая логика -
    * сервис должен отслеживать изменение баланса на кошельке, и делать "ветеор"
    * а именно сделать лотерею доступной с любого кошелька,
    * без какого либо визуального интерфейса - почти децентрализация,
    * где и что будет обрабатывать этот кошелек (адресс) ни кто знать не будет
    * отправили на него средства система сама решила выиграли что-то или нет
    * и дала ответ на кошелек в виде выигрыша или может быть супер приза и т д
    */
}
