package com.mindcube.testmod.server.service.repository;

import com.mindcube.testmod.server.service.entity.MessageEntity;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class MessageRepository extends BasicRepository {

    public MessageRepository(SessionFactory sessionFactory, Executor executor) {
        super(sessionFactory, executor);
    }

    public CompletableFuture<Void> insert(MessageEntity messageEntity) {
        return runAsync(session -> {
            var transaction = session.beginTransaction();
            session.persist(messageEntity);
            transaction.commit();
        });
    }

    public CompletableFuture<List<MessageEntity>> findAll() {
        return supplyAsync(session ->
            session.createQuery("from MessageEntity", MessageEntity.class).getResultList()
        );
    }
}
