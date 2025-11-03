package kr.spot.infrastructure.redis;

import java.util.List;
import kr.spot.application.ports.HotPostStore;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisHotPostStore implements HotPostStore {

    public static final int START = 0;
    public static final int END = 2;
    private static final String KEY = "popular:top3:total";

    private final StringRedisTemplate redis;

    @Override
    public void replaceTop3(List<Long> postIds) {
        redis.executePipelined(
                (RedisCallback<Object>) conn -> {
                    conn.del(KEY.getBytes());
                    for (Long id : postIds) {
                        conn.rPush(KEY.getBytes(), String.valueOf(id).getBytes());
                    }
                    return null;
                });
    }

    @Override
    public List<Long> getTop3() {
        List<String> vals = redis.opsForList().range(KEY, START, END);
        if (vals == null) {
            return List.of();
        }
        return vals.stream()
                .map(Long::valueOf)
                .toList();
    }
}
