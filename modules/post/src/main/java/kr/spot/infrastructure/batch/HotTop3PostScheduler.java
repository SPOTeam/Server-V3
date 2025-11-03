package kr.spot.infrastructure.batch;

import java.util.List;
import kr.spot.application.ports.HotPostStore;
import kr.spot.infrastructure.jpa.PostStatsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HotTop3PostScheduler {

    private final HotPostStore store;
    private final PostStatsRepository postStatsRepository;

    @Scheduled(cron = "0 0 13,18 * * *")
    public void refreshTop3() {
        List<Long> top3 = postStatsRepository.findTop3ByTotal();
        store.replaceTop3(top3);
    }
}
