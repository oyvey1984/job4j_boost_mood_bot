package ru.job4j.services;

import org.springframework.beans.factory.BeanNameAware;

public class ReminderService implements BeanNameAware {

    @Override
    public void setBeanName(String name) {
        System.out.println("Bean name in the context: " + name);
    }
}
