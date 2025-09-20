package com.sufi.module.service.availability;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.locks.ReentrantLock;

public class CsvLogger {

    private static final String FILE_NAME = "concurrent-Métrics.csv";
    private static final ReentrantLock lock = new ReentrantLock();

    static {
        // Cabecera CSV
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
            writer.write("timestamp,ciudad,cache_enabled,time_ms,response_size,success,error_type,status_code\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void log(Instant timestamp, String ciudad, boolean cache, long timeMs, int size,
                           boolean success, String errorType, int statusCode) {
        lock.lock();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
            writer.write(String.format("%s,%s,%b,%d,%d,%b,%s,%d\n",
                    timestamp, ciudad, cache, timeMs, size, success,
                    errorType != null ? errorType : "", statusCode));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}