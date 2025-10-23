package com.mindcube.testmod.server.service.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class BasicRepository {

    private final SessionFactory sessionFactory;
    private final Executor executor;

    protected BasicRepository(SessionFactory sessionFactory, Executor executor) {
        this.sessionFactory = sessionFactory;
        this.executor = executor;
    }

    public CompletableFuture<Void> runAsync(Consumer<Session> runnable) {
        return CompletableFuture.runAsync(() -> {
            try (var session = sessionFactory.openSession()) {
                runnable.accept(session);
            }
        }, executor);
    }

    public <T> CompletableFuture<T> supplyAsync(Function<Session, T> runnable) {
        return CompletableFuture.supplyAsync(() -> {
            try (var session = sessionFactory.openSession()) {
                return runnable.apply(session);
            }
        }, executor);
    }

    public void realise(){
        if(!sessionFactory.isClosed()){
            try {
                sessionFactory.close();
            } catch (Exception ignore) {
            }
        }
    }
}
