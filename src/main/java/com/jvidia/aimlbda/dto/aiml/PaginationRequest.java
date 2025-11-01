/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.dto.aiml;

import lombok.Data;

@Data // Lombok annotation
public class PaginationRequest {

    private Integer page = 0;
    private Integer size = 20;
}
