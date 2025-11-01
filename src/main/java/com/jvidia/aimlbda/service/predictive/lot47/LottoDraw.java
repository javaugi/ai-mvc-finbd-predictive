/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.service.predictive.lot47;

import java.util.Arrays;

/**
 *
 * @author javau
 */
public class LottoDraw {
    public int[] numbers;

    LottoDraw(int[] numbers) {
        this.numbers = numbers;
    }

    @Override
    public String toString() {
        return Arrays.toString(numbers);
    }
}
