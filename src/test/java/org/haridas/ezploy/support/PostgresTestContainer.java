package org.haridas.ezploy.support;

import org.testcontainers.containers.PostgreSQLContainer;

public class PostgresTestContainer
        extends PostgreSQLContainer<PostgresTestContainer> {

    private static final String IMAGE =
            "postgres:18";

    private static final PostgresTestContainer INSTANCE =
            new PostgresTestContainer();

    private PostgresTestContainer() {
        super(IMAGE);
    }

    public static PostgresTestContainer getInstance() {
        return INSTANCE;
    }

    @Override
    public void start() {
        super.start();

        System.setProperty(
                "DATABASE_URL",
                getJdbcUrl()
        );

        System.setProperty(
                "DATABASE_USER",
                getUsername()
        );

        System.setProperty(
                "DATABASE_PASS",
                getPassword()
        );
    }

    @Override
    public void stop() {
        // Do nothing.
        // Testcontainers will handle container lifecycle.
    }
}