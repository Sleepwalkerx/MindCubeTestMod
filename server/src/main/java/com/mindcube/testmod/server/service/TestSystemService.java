package com.mindcube.testmod.server.service;

import com.mindcube.testmod.common.TestMod;
import com.mindcube.testmod.common.network.ProtoMessagePacket;
import com.mindcube.testmod.server.service.entity.MessageEntity;
import com.mindcube.testmod.server.service.repository.MessageRepository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.service.ServiceRegistry;
import org.jetbrains.annotations.NotNull;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.*;
import java.util.function.Function;

public class TestSystemService {

    private final Gson gson = new GsonBuilder()
        .setPrettyPrinting()
        .disableHtmlEscaping()
        .create();

    private HikariDataSource dataSource;
    private MessageRepository messageRepository;
    private final ThreadPoolExecutor executor;

    public TestSystemService(){
        this.executor = new ThreadPoolExecutor(
            2,
            5,
            60,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(64),
            task -> {
                Thread t = new Thread(task);
                t.setName(TestMod.MOD_NAME + "-Db-Worker-" + t.threadId());
                t.setDaemon(true);
                return t;
            },
            new ThreadPoolExecutor.AbortPolicy()
        );
    }

    public void boostrap(){
        ServerLifecycleEvents.SERVER_STOPPED.register(minecraftServer -> realise());
    }

    private void initHikari(@NotNull ServiceConfig config){
        //Это всё можно создавать через xml файл, но ради демонстрации создаем сами, через json-конфиг
        var hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:postgresql://%s:%d/%s".formatted(config.getHost(), config.getPort(), config.getDatabase()));
        hikariConfig.setUsername(config.getUser());
        hikariConfig.setPassword(config.getPassword());
        hikariConfig.setDriverClassName("org.postgresql.Driver");
        hikariConfig.setMaximumPoolSize(12);
        hikariConfig.setMinimumIdle(2);
        hikariConfig.setPoolName(TestMod.MOD_NAME + "Pool");

        if(dataSource != null && !dataSource.isClosed()){
            try {
                dataSource.close();
            } catch (Exception ignore) {
            }
        } else  {
            dataSource = new HikariDataSource(hikariConfig);
        }
    }

    private void initHibernate(){
        var settings = new Properties();
        settings.put(AvailableSettings.DATASOURCE, dataSource);
        settings.put(AvailableSettings.HBM2DDL_AUTO, "update");
        if(FabricLoader.getInstance().isDevelopmentEnvironment()){
            settings.put(AvailableSettings.SHOW_SQL, true);
            settings.put(AvailableSettings.FORMAT_SQL, true);
        }

        ServiceRegistry registry = new StandardServiceRegistryBuilder()
            .applySettings(settings).build();

        SessionFactory sessionFactory = new MetadataSources(registry)
            .addAnnotatedClass(MessageEntity.class)
            .buildMetadata()
            .buildSessionFactory();
        messageRepository = new MessageRepository(sessionFactory, executor);
    }

    public void reload() throws IOException {
        var configFile = FabricLoader.getInstance().getConfigDir().resolve(TestMod.MOD_ID + ".json").toFile();
        if(!configFile.exists()){
            if(!configFile.createNewFile()){
                throw new IOException("Can't create ServiceConfig file");
            }
            try(FileWriter writer = new FileWriter(configFile)) {
                gson.toJson(new ServiceConfig(), writer);
            }
        }
        ServiceConfig config;
        try (FileReader reader = new FileReader(configFile)) {
            config = gson.fromJson(reader, ServiceConfig.class);
        }
        initHikari(config);
        initHibernate();
    }

    private void handleRequest(ServerPlayer sender, Function<MessageRepository, CompletableFuture<Void>> handler) {
        if(messageRepository == null){
            sender.sendSystemMessage(Component.translatable("response.testmod.not_ready").withStyle(ChatFormatting.RED));
        } else {
            try {
                handler.apply(messageRepository).exceptionally(exception -> {
                    sender.sendSystemMessage(Component.translatable("response.testmod.unexpected_error").withStyle(ChatFormatting.RED));
                    TestMod.LOGGER.error("Can't handle action", exception);
                    return null;
                });
            } catch (RejectedExecutionException e){
                sender.sendSystemMessage(Component.translatable("response.testmod.execution_rejected").withStyle(ChatFormatting.RED));
            }
        }
    }

    public void handleProtoMessage(ProtoMessagePacket message, ServerPlayer sender){
        handleRequest(sender, messageRepository -> messageRepository
            .insert(new MessageEntity(sender.getGameProfile().getId(), message.message().getText()))
            .thenRun(() ->
                sender.sendSystemMessage(Component.translatable("response.testmod.proto_message.processed").withStyle(ChatFormatting.YELLOW))
            )
        );
    }

    private void realise(){
        if(dataSource != null && !dataSource.isClosed()) {
            try {
               dataSource.close();
            } catch (Exception ignore) {
            }
        }
        if(messageRepository != null) {
            messageRepository.realise();
        }
    }
}
