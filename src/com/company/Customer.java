package com.company;

import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Customer extends Thread
{
    private ReentrantLock locker = new ReentrantLock(); // создаем заглушку
    private  Condition condition = locker.newCondition();
    volatile List<Cashier> cashiers;
    int taskQty;
    Cashier currentCashier;
    volatile boolean isServed;

    public Customer(String name, List<Cashier> cashiers, int taskQty) {
        super(name);
        this.cashiers = cashiers;
        this.taskQty = taskQty;
        isServed = false;
        start();//запуск потока
    }

    public int getTaskQty() {
        return taskQty;
    }



    public void setServed(boolean isServed) {
        this.isServed = isServed;
    }

    //найти кратчайшую очередь
    private int findShortestQueue(int shortestQueueId) {
        int shortestQueueLength = Integer.MAX_VALUE;
        for (int i = 0; i < cashiers.size(); i++) {
            int currentQueueLength = cashiers.get(i).getCustomerQueue().size();
            if (currentQueueLength == 0) {
                shortestQueueId = i;
                shortestQueueLength = 0;
                break;
            } else {
                if (currentQueueLength < shortestQueueLength) {
                    shortestQueueId = i;
                    shortestQueueLength = currentQueueLength;
                }
            }
        }
        return shortestQueueId;
    }
      //найти свободную очередь
    private Cashier findFreeQueue() {
        Cashier freeCashier = currentCashier;
        for (int i = 0; i < cashiers.size(); i++) {
            int currentQueueLength = cashiers.get(i).getCustomerQueue().size();
            if (currentQueueLength == 0) {
                return freeCashier = cashiers.get(i);
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public void run() {

        try {
            int shortestQueueId = findShortestQueue(0);
            currentCashier = cashiers.get(shortestQueueId);
            currentCashier.enqueueCustomer(this);

            while (!isServed) {
                if (this != currentCashier.getCustomerQueue().peek()) {
                    Cashier oldCashier = null;

                    locker.lock(); // устанавливаем блокировку
                     {
                        Cashier bufferCashier = findFreeQueue();
                        if (bufferCashier != null) {
                            oldCashier = currentCashier;
                            currentCashier = bufferCashier;
                        }
                         locker.unlock(); // снимаем блокировку
                        if (oldCashier != null) {
                            oldCashier.dequeueCustomer(this);
                            currentCashier.enqueueCustomer(this);
                        }
                    }
                }

            }
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
        finally{
            System.out.println(getTaskQty() + " задание " + Thread.currentThread().getName() + " обслуживала " + currentCashier);
        }


    }

}
