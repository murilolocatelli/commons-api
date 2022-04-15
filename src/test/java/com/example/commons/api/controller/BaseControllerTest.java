package com.example.commons.api.controller;

import com.example.commons.api.exception.EntityNotFoundException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class BaseControllerTest {

    @Test
    public void buildResponseHttpStatusNull() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ConcreteController concreteController = new ConcreteController();

            concreteController.buildResponseHttpStatusNull();
        });
    }

    @Test
    public void buildResponseResultNull() {
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            ConcreteController concreteController = new ConcreteController();

            concreteController.buildResponseResultNull();
        });
    }

    @Test
    public void buildResponseResultObject() {
        ConcreteController concreteController = new ConcreteController();

        concreteController.buildResponseResultObject();

        // TODO: assert
    }

    //TODO: other tests

}
