package com.company;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Cashier extends Thread {



    volatile private LinkedList<Customer> customerQueue = new LinkedList<>();
    private ReentrantLock locker = new ReentrantLock(); // создаем заглушку
    private Condition condition = locker.newCondition();


    public Cashier(String name) {
        super(name);
        this.setDaemon(true);
        start();
    }

    public LinkedList<Customer> getCustomerQueue() {
        return customerQueue;
    }

    //поставить в очередь клиента
    public  void enqueueCustomer(Customer customer){
       locker.lock(); // устанавливаем блокировку
        customerQueue.addLast(customer);
        condition.signalAll();
        locker.unlock(); // снимаем блокировку
    }

    ////убрать в очередь клиента
    public  void dequeueCustomer(Customer customer){
        locker.lock(); // устанавливаем блокировку
        customerQueue.remove(customer);
        condition.signalAll();
        locker.unlock(); // снимаем блокировку
    }

    @Override
    public void run() {

        while (true) {
            try {
                locker.lock(); // устанавливаем блокировку
                while (customerQueue.size() == 0) {
                    condition.await(); //поток ожидает, пока не будет выполнено условие
                }
                locker.unlock(); // снимаем блокировку
                Customer currentCustomer;

                locker.lock(); // устанавливаем блокировку
                currentCustomer = customerQueue.peek();
                {
                System.out.println(this + " начала обслуживать " + currentCustomer);
                Thread.sleep(500 * currentCustomer.getTaskQty());
                currentCustomer.setServed(true);
                condition.signalAll();
                //сигнализирует всем потокам, у которых ранее был вызван метод await(), что они могут продолжить работу.
            }
                locker.unlock(); // снимаем блокировку

                locker.lock(); // устанавливаем блокировку
                    customerQueue.poll();
                locker.unlock(); // снимаем блокировку

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public String toString() {
        return getName();
    }
}