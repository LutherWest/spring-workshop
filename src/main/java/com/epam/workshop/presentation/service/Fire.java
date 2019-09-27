package com.epam.workshop.presentation.service;

import org.springframework.stereotype.Service;

@SpecialAnnotation("hello!")
@Service
public class Fire implements Runnable {

    public void fire() {
        System.out.println("Fire!");
    }

    @Override
    public void run() {
        /*NOP*/
    }
}
