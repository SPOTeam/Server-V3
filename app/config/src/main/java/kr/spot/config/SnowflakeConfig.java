package kr.spot.config;

import kr.spot.IdGenerator;
import kr.spot.impl.Snowflake;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
public class SnowflakeConfig {

    @Component
    public static class SnowflakeIdGenerator implements IdGenerator {
        private final Snowflake snowflake = new Snowflake();

        @Override
        public long nextId() {
            return snowflake.nextId();
        }
    }
}
