package com.company;

import java.util.LinkedList;
import java.util.List;


       /* Разработать многопоточное приложение.
        Использовать возможности, предоставляемые пакетом java.util.concurrent.
        Не использовать слово synchronized.
        Все сущности, желающие получить доступ к ресурсу, должны быть потоками.
        Использовать возможности ООП.
        Не использовать графический интерфейс. Приложение должно быть консольным.


        28 Свободная касса. В ресторане быстрого обслуживания есть несколько касс.
        Посетители стоят в очереди в конкретную кассу, но могут перейти в другую очередь
        при уменьшении или исчезновении там очереди.*/

public class Main {

    public static void main(String[] args) throws InterruptedException {
        List<Cashier> cashiers = new LinkedList<>();
        List<Customer> customers = new LinkedList<Customer>();

        for (int i = 0; i < 3; i++) {
            cashiers.add(new Cashier("Касса "+ i));
        }

        for (int i = 0; i < 6; i++) {
            customers.add(new Customer("Посетителя " + i, cashiers, 1 +  (int) (3 * Math.random())));
        }

        for (Customer customer : customers){
            customer.join();//ожидает завершения  потока исполнения.
        }

        System.out.println("Все клиенты были обслужены\n");


    }
}
