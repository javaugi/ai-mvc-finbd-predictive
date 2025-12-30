/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.actuator.benchmark;

import com.jvidia.aimlbda.service.TestUserService;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
//import org.openjdk.jmh.annotations.*;

/*
✅ Even better for your case (Spring Boot + React + PostgreSQL)
You should use real app benchmarks instead of Shade:

What to measure         Best Tool For You
Web API latency         k6 or Gatling
JVM CPU/memory          VisualVM / JFR
DB time                 pg_stat_statements
JVM GC                  -Xlog:gc*
Real metrics            Prometheus + Grafana (which you already use)
 */
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 2)
@Measurement(iterations = 5)
@Fork(1)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@Component
public class BenchmarkTestuserService {

    @Autowired
    private TestUserService testUserService;

    /*
    @Setup
    public void setup() {
        testUserService = new TestUserService();
    }
    // */

    @Benchmark
    public void testPerformance() {
        testUserService.getAll();
    }

}
