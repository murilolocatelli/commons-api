package com.example.commons.api.controller;

import com.example.commons.api.exception.EntityNotFoundException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class BaseControllerTest {

    @Test
    void buildResponseHttpStatusNull() {
        ConcreteController concreteController = new ConcreteController();

        Assertions.assertThrows(
            IllegalArgumentException.class, () -> concreteController.buildResponseHttpStatusNull());
    }

    @Test
    void buildResponseResultNull() {
        ConcreteController concreteController = new ConcreteController();

        Assertions.assertThrows(
            EntityNotFoundException.class, () -> concreteController.buildResponseResultNull());
    }

    @Test
    void buildResponseResultObject() {
        ConcreteController concreteController = new ConcreteController();

        concreteController.buildResponseResultObject();

        // TODO: assert
    }

    //TODO: other tests

}
