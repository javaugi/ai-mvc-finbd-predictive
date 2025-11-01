/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.utils;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.Value;

/**
 *
 * @author javaugi
 */
public class LombokExamples  {
/*
To create immutable objects with Lombok's @Builder, you need to combine it with other Lombok annotations and follow specific practices. Here's how:
    1. Use @Value or @Getter with final fields:
        @Value: This annotation makes all fields private and final by default, and it does not generate setters. It also generates constructors, equals(), hashCode(), and toString() methods.
        @Getter with final fields: If you need more control over constructor generation, use @Getter and declare all fields as final.
    2. Apply @Builder:
        `Add the @Builder annotation to your class. This will generate a builder class and a builder() method for creating instances of your class.
*/


    public static void main(String[] args) {
        example1();
        example2();
        
        /*
    Choosing the right approach:
            If you need to modify multiple fields at once, the @Builder approach with toBuilder() is generally more flexible.
            If you only need to modify a single field, @Value with @With can be a simpler alternative. 
        Both approaches provide efficient ways to create copies of immutable objects while maintaining immutability        
        */
    }

    private static void example1() {
        ImmutableObject original = ImmutableObject.builder().id(1L).name("Alice").age(30).build();
        System.out.println(original);

        // Create a copy with a different ID
        ImmutableObject copied = original.toBuilder().id(2L).build();
        //The toBuilder() method allows you to quickly create a builder that's pre-populated with the original object's data.
        //It's important to use @Builder with immutable objects, as it's designed to create new instances instead of modifying existing ones. 
        System.out.println(copied);
        /*
        original.toBuilder(): This line initiates the builder with the values of the original object.
        .id(2L): This line overrides the id field with the new value (2L).
        .build(): This completes the build, creating a new immutable object with the overridden ID and the remaining fields copied from the original.         
        */
    }

    private static void example2() {
        ImmutableObject2 original = new ImmutableObject2(1L, "Alice", 30);
        System.out.println(original);

        //ImmutableObject copied = original.withAge(35); // Use withAge to create a new object with the changed age
        //System.out.println(copied);
        /*
        @With generates withXXX() methods for each field, which return a new object with the updated field value.
        This approach is more concise but requires using @With for every field you want to potentially change.         
        */
    }

    @Getter
    @ToString
    @Builder(toBuilder = true) // Add toBuilder to the @Builder annotation 
    // @Builder(toBuilder = true): This annotation instructs Lombok to generate an inner builder class and the toBuilder() method, 
    //      which allows you to create a builder initialized with the existing object's values.
    public static final class ImmutableObject {

        private final Long id;
        private final String name;
        private final int age;

    }

    @Value
    @EqualsAndHashCode
    @ToString
    //@Value automatically generates constructors, getters, toString(), equals(), and hashCode() methods.
    public static final class ImmutableObject2 {

        private final Long id;
        private final String name;
        private final int age;

    }
}
